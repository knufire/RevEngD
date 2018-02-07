package odyssey.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import odyssey.renderers.ClassRenderer;
import odyssey.renderers.CommentMessageRenderer;
import odyssey.renderers.InitMessageRenderer;
import odyssey.renderers.MessageRenderer;
import odyssey.renderers.RelationshipRenderer;
import odyssey.renderers.ReturnMessageRenderer;

public class RendererRelfectionModule extends AbstractModule {

  protected void configure() {

  }

  @Provides
  @Named("relationship_renderers")
  Map<String, RelationshipRenderer> createRelationshipRenderers() {
    Map<String, RelationshipRenderer> relationshipRenderers = new HashMap<>();
    addDefaultRelationshipRenderers(relationshipRenderers);

    Collection<RelationshipRenderer> renderers = reflectRelationshipRenderers();

    renderers.forEach(r -> {
      relationshipRenderers.put(r.getName(), r);
    });
    return relationshipRenderers;
  }

  private void addDefaultRelationshipRenderers(Map<String, RelationshipRenderer> relationshipRenderers) {
    relationshipRenderers.put("default", new RelationshipRenderer());
  }

  private Collection<RelationshipRenderer> reflectRelationshipRenderers() {
    String renderers = System.getProperty("-relationshipRenderers");

    try {
      String[] tokens = renderers.split(" ");
      return reflectRelationshipRenderers(tokens);
    } catch (NullPointerException e) {
      return Collections.emptyList();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<RelationshipRenderer> reflectRelationshipRenderers(String[] rendererNames)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    List<RelationshipRenderer> renderers = new ArrayList<>();
    RelationshipRenderer renderer;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (RelationshipRenderer) Class.forName(rendererNames[i]).newInstance();
      renderers.add(renderer);
    }
    return renderers;
  }

  @Provides
  @Named("message_renderers")
  Map<String, MessageRenderer> getMessageRenderers() {
    Map<String, MessageRenderer> messageRenderers = new HashMap<>();
    Collection<MessageRenderer> renderers = reflectMessageRenderers();
    addDefaultMessageRenderers(messageRenderers);

    renderers.forEach(r -> {
      messageRenderers.put(r.getName(), r);
    });
    return messageRenderers;
  }

  private void addDefaultMessageRenderers(Map<String, MessageRenderer> messageRenderers) {
    messageRenderers.put("return", new ReturnMessageRenderer());
    messageRenderers.put("init", new InitMessageRenderer());
    messageRenderers.put("comment", new CommentMessageRenderer());
    messageRenderers.put("default", new MessageRenderer());
  }

  private Collection<MessageRenderer> reflectMessageRenderers() {
    String renderers = System.getProperty("-messageRenderers");
    try {
      String[] tokens = renderers.split(" ");
      return reflectMessageRenderers(tokens);
    } catch (NullPointerException e) {
      return Collections.emptyList();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<MessageRenderer> reflectMessageRenderers(String[] rendererNames)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    List<MessageRenderer> renderers = new ArrayList<>();
    MessageRenderer renderer;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (MessageRenderer) Class.forName(rendererNames[i]).newInstance();
      renderers.add(renderer);
    }
    return renderers;
  }

  @Provides
  @Named("class_renderers")
  Map<String, ClassRenderer> getClassRenderers() {
    Map<String, ClassRenderer> classRenderers = new HashMap<>();
    Collection<ClassRenderer> renderers = reflectClassRenderers();
    addDefaultClassRenderers(classRenderers);

    renderers.forEach(r -> {
      classRenderers.put(r.getName(), r);
    });
    return classRenderers;
  }

  private void addDefaultClassRenderers(Map<String, ClassRenderer> classRenderers) {
    classRenderers.put("default", new ClassRenderer());
  }

  private Collection<ClassRenderer> reflectClassRenderers() {
    String renderers = System.getProperty("-classRenderers");
    try {
      String[] tokens = renderers.split(" ");
      return reflectClassRenderers(tokens);
    } catch (NullPointerException e) {
      return Collections.emptyList();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<ClassRenderer> reflectClassRenderers(String[] rendererNames)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    List<ClassRenderer> renderers = new ArrayList<>();
    ClassRenderer renderer;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (ClassRenderer) Class.forName(rendererNames[i]).newInstance();
      renderers.add(renderer);
    }
    return renderers;
  }

}
