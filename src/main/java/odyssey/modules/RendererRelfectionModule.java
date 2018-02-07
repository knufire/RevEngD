package odyssey.modules;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import odyssey.renderers.ClassRenderer;
import odyssey.renderers.MessageRenderer;
import odyssey.renderers.RelationshipRenderer;

public class RendererRelfectionModule extends AbstractModule {

  protected void configure() {

  }

  @Provides
  @Named("relationship_renderers")
  Map<String, RelationshipRenderer> createRelationshipRenderers() {
    Map<String, RelationshipRenderer> relationshipRenderers = new HashMap<>();
    Collection<RelationshipRenderer> renderers = reflectRelationshipRenderers();
    
    return relationshipRenderers;
  }

  private List<RelationshipRenderer> reflectRelationshipRenderers() {
    return null;
  }

  @Provides
  @Named("message_renderers")
  Map<String, MessageRenderer> getMessageRenderers() {
    return reflectMessageRenderers();
  }

  private Map<String, MessageRenderer> reflectMessageRenderers() {
    return null;
  }

  @Provides
  @Named("class_renderers")
  Map<String, ClassRenderer> getClassRenderers() {
    return reflectClassRenderers();
  }

  private Map<String, ClassRenderer> reflectClassRenderers() {
    return null;
  }

}
