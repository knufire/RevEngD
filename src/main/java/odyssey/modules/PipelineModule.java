package odyssey.modules;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import odyssey.analyzers.Analyzer;
import odyssey.analyzers.AnalyzerBundle;
import odyssey.analyzers.AncestorAnalyzer;
import odyssey.analyzers.AssociationAnalyzer;
import odyssey.analyzers.DependencyAnalyzer;
import odyssey.analyzers.EmptyAnalyzer;
import odyssey.analyzers.InheritanceAnalyzer;
import odyssey.analyzers.SceneAnalyzer;
import odyssey.analyzers.SequenceAnalyzer;
import odyssey.analyzers.SootAnalyzer;
import odyssey.analyzers.UMLAnalyzer;
import odyssey.filters.ClassNameFilter;
import odyssey.filters.ClinitFilter;
import odyssey.filters.DollarSignFilter;
import odyssey.filters.Filter;
import odyssey.filters.JDKFilter;
import odyssey.filters.NullSuperFilter;
import odyssey.filters.PackagePrivateFilter;
import odyssey.filters.ProtectedFilter;
import odyssey.filters.PublicFilter;
import odyssey.filters.RelationshipFilter;
import odyssey.methodresolution.AggregateAlgorithm;
import odyssey.methodresolution.AggregationStrategy;
import odyssey.methodresolution.Algorithm;

public class PipelineModule extends AbstractModule {

  private AnalyzerBundle bundle;
  private boolean includeLambda;

  public PipelineModule() {
    bundle = new AnalyzerBundle();
    includeLambda = Boolean.parseBoolean(System.getProperty("--exclude-lambda"));
  }

  protected void configure() {
  }

  @Provides
  AnalyzerBundle getBundle() {
    return bundle;
  }

  @Provides
  @Named("pipeline")
  List<Analyzer> createPipeline(@Named("analyzers") Queue<Analyzer> userAnalyzers) {
    List<Analyzer> pipeline = new ArrayList<>();
    pipeline.add(createSceneAnalyzer());
    pipeline.add(createSootAnalyzer());
    pipeline.add(createAncestorAnalyzer());
    pipeline.add(createInheritanceAnalyzer());
    pipeline.add(createAssociationAnalyzer());
    pipeline.add(createDependencyAnalyzer());
    pipeline.addAll(userAnalyzers);
    pipeline.add(createSequenceAnalyzer());
    pipeline.add(createUMLAnalyzer());
    return pipeline;
  }

  private Analyzer createSceneAnalyzer() {
    return new SceneAnalyzer(Collections.emptyList());
  }

  private Analyzer createSootAnalyzer() {
    List<Filter> sootAnalyzerFilters = new ArrayList<Filter>();
    sootAnalyzerFilters.add(new NullSuperFilter());
    sootAnalyzerFilters.add(new ClassNameFilter());
    return new SootAnalyzer(sootAnalyzerFilters);
  }

  private Analyzer createAncestorAnalyzer() {
    if (Boolean.parseBoolean(System.getProperty("--include-ancestors"))) {
      return new AncestorAnalyzer(Collections.emptyList());
    }
    return new EmptyAnalyzer(Collections.emptyList());
  }

  private Analyzer createInheritanceAnalyzer() {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    if (includeLambda) {
      relationShipFilters.add(new DollarSignFilter());
    }
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    return new InheritanceAnalyzer(relationShipFilters);
  }

  private Analyzer createDependencyAnalyzer() {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    addModifierFilter(relationShipFilters);
    if (includeLambda) {
      relationShipFilters.add(new DollarSignFilter());
    }
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    return new DependencyAnalyzer(relationShipFilters);
  }

  private Analyzer createAssociationAnalyzer() {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    addModifierFilter(relationShipFilters);
    if (includeLambda) {
      relationShipFilters.add(new DollarSignFilter());
    }
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    return new AssociationAnalyzer(relationShipFilters);
  }

  private Analyzer createUMLAnalyzer() {
    List<Filter> UMLFilters = new ArrayList<Filter>();
    addModifierFilter(UMLFilters);
    if (includeLambda) {
      UMLFilters.add(new DollarSignFilter());
    }
    UMLFilters.add(new ClinitFilter());
    return new UMLAnalyzer(UMLFilters);
  }

  private Analyzer createSequenceAnalyzer() {
    try {
      return createSequenceAnalyzerHelper();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private Algorithm createMethodResolver() throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    String[] algorithms = System.getProperty("-mra").split(" ");
    if (algorithms.length == 1) {
      return getClassFromName(Algorithm.class, algorithms[1]);
    } else {
      Class<AggregateAlgorithm> aggregate = AggregateAlgorithm.class;
      List<Algorithm> singleAlgorithms = new ArrayList<>();
      for (int i = 0; i < algorithms.length; i++) {
        singleAlgorithms.add(getClassFromName(Algorithm.class, algorithms[i]));
      }
      AggregationStrategy strat = getClassFromName(AggregationStrategy.class, System.getProperty("-mrs"));
      AggregateAlgorithm aggregateAlgorithm = aggregate.getConstructor(AggregationStrategy.class).newInstance(strat);
      for (Algorithm a : singleAlgorithms) {
        aggregateAlgorithm.addAlgorithm(a);
      }
      return aggregateAlgorithm;
    }
  }

  private Analyzer createSequenceAnalyzerHelper() throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    if (System.getProperty("-e").length() > 0) {
      List<Filter> sequenceFilters = new ArrayList<Filter>();
      addModifierFilter(sequenceFilters);
      if (includeLambda) {
        sequenceFilters.add(new DollarSignFilter());
      }
      sequenceFilters.add(new ClinitFilter());
      if (!Boolean.parseBoolean(System.getProperty("--expand-jdk"))) {
        sequenceFilters.add(new JDKFilter());
      }
      return new SequenceAnalyzer(sequenceFilters, createMethodResolver());
    } else {
      return new EmptyAnalyzer(Collections.emptyList());
    }
  }

  private <T> T getClassFromName(Class<T> clazz, String name) {
    try {
      @SuppressWarnings("unchecked")
      Class<T> algorithm = (Class<T>) Class.forName(name);
      return algorithm.newInstance();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException
        | ClassNotFoundException e) {
      System.err.println("Could not instantiate method resolver algorithm class.");
      e.printStackTrace();
    }
    return null;
  }

  private void addModifierFilter(List<Filter> filters) {
    String mod = System.getProperty("-a");
    switch (mod) {
    case "protected":
      filters.add(new ProtectedFilter());
      return;
    case "public":
      filters.add(new PublicFilter());
      return;
    case "package":
      filters.add(new PackagePrivateFilter());
      return;
    default: // "private" by default, therefore no filter
      return;
    }

  }

}
