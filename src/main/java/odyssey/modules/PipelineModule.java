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
    createSceneAnalyzer(pipeline);
    createSootAnalyzer(pipeline);
    createAncestorAnalyzer(pipeline);
    createInheritanceAnalyzer(pipeline);
    createAssociationAnalyzer(pipeline);
    createDependencyAnalyzer(pipeline);
    createMessageAnalyzer(algo, pipeline);

    pipeline.addAll(userAnalyzers);

    createSequenceAnalyzer(messageRenderers, pipeline);
    createUMLAnalyzer(classRenderers, relationshipRenderers, pipeline);

    createFileWriterAnalyzer(pipeline);
    return pipeline;
  }

  private void createSceneAnalyzer(List<Analyzer> pipeline) {
    pipeline.add(new SceneAnalyzer(Collections.emptyList()));
  }

  private void createSootAnalyzer(List<Analyzer> pipeline) {
    List<Filter> sootAnalyzerFilters = new ArrayList<Filter>();
    sootAnalyzerFilters.add(new NullSuperFilter());
    sootAnalyzerFilters.add(new ClassNameFilter());
    pipeline.add(new SootAnalyzer(sootAnalyzerFilters));
  }

  private void createAncestorAnalyzer(List<Analyzer> pipeline) {
    if (Boolean.parseBoolean(System.getProperty("--include-ancestors"))) {
      pipeline.add(new AncestorAnalyzer(Collections.emptyList()));
    }
  }

  private void createInheritanceAnalyzer(List<Analyzer> pipeline) {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    if (includeLambda) {
      relationShipFilters.add(new DollarSignFilter());
    }
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    pipeline.add(new InheritanceAnalyzer(relationShipFilters));
  }

  private void createDependencyAnalyzer(List<Analyzer> pipeline) {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    addModifierFilter(relationShipFilters);
    if (includeLambda) {
      relationShipFilters.add(new DollarSignFilter());
    }
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    pipeline.add(new DependencyAnalyzer(relationShipFilters));
  }

  private void createAssociationAnalyzer(List<Analyzer> pipeline) {
    List<Filter> relationShipFilters = new ArrayList<Filter>();
    addModifierFilter(relationShipFilters);
    if (includeLambda) {
      relationShipFilters.add(new DollarSignFilter());
    }
    relationShipFilters.add(new RelationshipFilter(this.bundle));
    pipeline.add(new AssociationAnalyzer(relationShipFilters));
  }

  private void createUMLAnalyzer(Map<String, ClassRenderer> classRenderers,
      Map<String, RelationshipRenderer> relationshipRenderers, List<Analyzer> pipeline) {

    List<Filter> UMLFilters = new ArrayList<Filter>();
    addModifierFilter(UMLFilters);
    if (includeLambda) {
      UMLFilters.add(new DollarSignFilter());
    }
    UMLFilters.add(new ClinitFilter());
    pipeline.add(new UMLAnalyzer(UMLFilters, classRenderers, relationshipRenderers));
  }

  private void createMessageAnalyzer(Algorithm algo, List<Analyzer> pipeline) {
    if (System.getProperty("-e").length() > 0) {
      pipeline.add(createMessageAnalyzerHelper(algo));
    }
  }

  private Analyzer createMessageAnalyzerHelper(Algorithm algo) {
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
  }

  private void createSequenceAnalyzer(Map<String, MessageRenderer> messageRenderers, List<Analyzer> pipeline) {
    if (System.getProperty("-e").length() > 0) {
      pipeline.add(new SequenceAnalyzer(Collections.emptyList(), messageRenderers));
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

  private void createFileWriterAnalyzer(List<Analyzer> pipeline) {
    pipeline.add(new FileWriterAnalyzer(Collections.emptyList()));
  }

}
