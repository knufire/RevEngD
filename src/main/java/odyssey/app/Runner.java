package odyssey.app;

import com.google.inject.Guice;
import com.google.inject.Injector;

import odyssey.modules.PipelineModule;
import odyssey.modules.ReflectionModule;
import odyssey.modules.RendererRelfectionModule;

public class Runner {

  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

    PropertiesSetter.set(args);
    Injector injector = Guice.createInjector(new PipelineModule(), new ReflectionModule(), new RendererRelfectionModule());
    Processor p = injector.getInstance(Processor.class);
    p.executePipeline();

  }

}
