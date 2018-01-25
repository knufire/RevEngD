package odyssey.app;

import com.google.inject.Guice;
import com.google.inject.Injector;

import csse374.revengd.examples.fixtures.ICalculator;
import odyssey.modules.PipelineModule;
import odyssey.modules.ReflectionModule;
import polyglot.ext.jl.ast.Special_c;

public class Runner {

  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
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
