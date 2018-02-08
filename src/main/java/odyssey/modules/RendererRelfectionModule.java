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
import odyssey.renderers.IClassRenderer;
import odyssey.renderers.IMessageRenderer;
import odyssey.renderers.IRelationshipRenderer;
import odyssey.renderers.InitMessageRenderer;
import odyssey.renderers.MessageRenderer;
import odyssey.renderers.RelationshipRenderer;
import odyssey.renderers.ReturnMessageRenderer;

public class RendererRelfectionModule extends AbstractModule {

  protected void configure() {

  }

  @Provides
  @Named("relationship_renderers")
  Map<String, IRelationshipRenderer> createRelationshipRenderers() {
    Map<String, IRelationshipRenderer> relationshipRenderers = new HashMap<>();
    addDefaultRelationshipRenderers(relationshipRenderers);

    Collection<IRelationshipRenderer> renderers = reflectRelationshipRenderers();

    renderers.forEach(r -> {
      relationshipRenderers.put(r.getName(), r);
    });
    return relationshipRenderers;
  }

  private void addDefaultRelationshipRenderers(Map<String, IRelationshipRenderer> relationshipRenderers) {
    relationshipRenderers.put("default", new RelationshipRenderer());
  }

  private Collection<IRelationshipRenderer> reflectRelationshipRenderers() {
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

  private Collection<IRelationshipRenderer> reflectRelationshipRenderers(String[] rendererNames)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    List<IRelationshipRenderer> renderers = new ArrayList<>();
    IRelationshipRenderer renderer;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (IRelationshipRenderer) Class.forName(rendererNames[i]).newInstance();
      renderers.add(renderer);
    }
    return renderers;
  }

  @Provides
  @Named("message_renderers")
  Map<String, IMessageRenderer> getMessageRenderers() {
    Map<String, IMessageRenderer> messageRenderers = new HashMap<>();
    Collection<IMessageRenderer> renderers = reflectMessageRenderers();
    addDefaultMessageRenderers(messageRenderers);

    renderers.forEach(r -> {
      messageRenderers.put(r.getName(), r);
    });
    return messageRenderers;
  }

  private void addDefaultMessageRenderers(Map<String, IMessageRenderer> messageRenderers) {
    messageRenderers.put("return", new ReturnMessageRenderer());
    messageRenderers.put("init", new InitMessageRenderer());
    messageRenderers.put("comment", new CommentMessageRenderer());
    messageRenderers.put("default", new MessageRenderer());
  }

  private Collection<IMessageRenderer> reflectMessageRenderers() {
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

  private Collection<IMessageRenderer> reflectMessageRenderers(String[] rendererNames)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    List<IMessageRenderer> renderers = new ArrayList<>();
    IMessageRenderer renderer;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (IMessageRenderer) Class.forName(rendererNames[i]).newInstance();
      renderers.add(renderer);
    }
    return renderers;
  }

  @Provides
  @Named("class_renderers")
  Map<String, IClassRenderer> getClassRenderers() {
    Map<String, IClassRenderer> classRenderers = new HashMap<>();
    Collection<IClassRenderer> renderers = reflectClassRenderers();
    addDefaultClassRenderers(classRenderers);

    renderers.forEach(r -> {
      classRenderers.put(r.getName(), r);
    });
    return classRenderers;
  }

  private void addDefaultClassRenderers(Map<String, IClassRenderer> classRenderers) {
    classRenderers.put("default", new ClassRenderer());
  }

  private Collection<IClassRenderer> reflectClassRenderers() {
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

  private Collection<IClassRenderer> reflectClassRenderers(String[] rendererNames)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    List<IClassRenderer> renderers = new ArrayList<>();
    IClassRenderer renderer;
    for (int i = 0; i < rendererNames.length; i++) {
      renderer = (IClassRenderer) Class.forName(rendererNames[i]).newInstance();
      renderers.add(renderer);
    }
    return renderers;
  }

}
