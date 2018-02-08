package odyssey.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import odyssey.analyzers.FileWriterAnalyzer;
import odyssey.analyzers.InheritanceAnalyzer;
import odyssey.analyzers.MessageAnalyzer;
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
import odyssey.methodresolution.Algorithm;
import odyssey.renderers.ClassRenderer;
import odyssey.renderers.MessageRenderer;
import odyssey.renderers.RelationshipRenderer;

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
  List<Analyzer> createPipeline(@Named("analyzers") Queue<Analyzer> userAnalyzers,
      @Named("relationship_renderers") Map<String, RelationshipRenderer> relationshipRenderers,
      @Named("message_renderers") Map<String, MessageRenderer> messageRenderers,
      @Named("class_renderers") Map<String, ClassRenderer> classRenderers, @Named("Resolution") Algorithm algo) {
    List<Analyzer> pipeline = new ArrayList<>();
    pipeline.add(createSceneAnalyzer());
    pipeline.add(createSootAnalyzer());
    pipeline.add(createAncestorAnalyzer());
    pipeline.add(createInheritanceAnalyzer());
    pipeline.add(createAssociationAnalyzer());
    pipeline.add(createDependencyAnalyzer());
    pipeline.add(createMessageAnalyzer(algo));

    pipeline.addAll(userAnalyzers);

    pipeline.add(createSequenceAnalyzer(messageRenderers));
    pipeline.add(createUMLAnalyzer(classRenderers, relationshipRenderers));

    pipeline.add(createFileWriterAnalyzer());
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

  private Analyzer createUMLAnalyzer(Map<String, ClassRenderer> classRenderers,
      Map<String, RelationshipRenderer> relationshipRenderers) {

    List<Filter> UMLFilters = new ArrayList<Filter>();
    addModifierFilter(UMLFilters);
    if (includeLambda) {
      UMLFilters.add(new DollarSignFilter());
    }
    UMLFilters.add(new ClinitFilter());
    return new UMLAnalyzer(UMLFilters, classRenderers, relationshipRenderers);
  }

  private Analyzer createMessageAnalyzer(Algorithm algo) {
    return createMessageAnalyzerHelper(algo);
  }

  private Analyzer createMessageAnalyzerHelper(Algorithm algo) {
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
      return new MessageAnalyzer(sequenceFilters, algo);
    } else {
      return new EmptyAnalyzer(Collections.emptyList());
    }
  }
  
  private Analyzer createSequenceAnalyzer(Map<String, MessageRenderer> messageRenderers) {
    if (System.getProperty("-e").length() > 0) {
      return new SequenceAnalyzer(Collections.emptyList(), messageRenderers);
    } else {
      return new EmptyAnalyzer(Collections.emptyList());
    }
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

  private Analyzer createFileWriterAnalyzer() {
    return new FileWriterAnalyzer(Collections.emptyList());
  }

}
