package odyssey.app;

import com.google.inject.Guice;
import com.google.inject.Injector;

import odyssey.modules.PipelineModule;
import odyssey.modules.ReflectionModule;

public class Runner {

  public static void main(String[] args) {
    @SuppressWarnings("unchecked")
    Class<? extends ICalculator> clazz = (Class<? extends ICalculator>) Class
        .forName("csse374.revengd.detectors.SpecialCalculatorC");
    ICalculator calculator = clazz.newInstance();
    System.out.println("Class loaded successfully! Result: " + calculator.multiply(1, 2, 3));

    PropertiesSetter.set(args);
    Injector injector = Guice.createInjector(new PipelineModule(), new ReflectionModule());
    Processor p = injector.getInstance(Processor.class);
    p.executePipeline();

  }

}
