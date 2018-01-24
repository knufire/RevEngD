package odyssey.app;

import com.google.inject.Guice;
import com.google.inject.Injector;

import odyssey.modules.PipelineModule;
import odyssey.modules.ReflectionModule;

public class Runner {

  public static void main(String[] args) {
    
    PropertiesSetter.set(args);
     Injector injector = Guice.createInjector(new PipelineModule(), new ReflectionModule());
     Processor p = injector.getInstance(Processor.class);
     p.executePipeline();

  }
  

  

}
