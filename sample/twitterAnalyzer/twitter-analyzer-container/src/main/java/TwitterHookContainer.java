import com.piranframework.ganjex.api.ServiceContext;
import com.piranframework.ganjex.api.ShutdownHook;
import com.piranframework.ganjex.api.StartupHook;
import com.sample.twitter.AnalyzerMethod;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TwitterHookContainer {

    public static final Map<String, Set<Method>> ANALYZERS = new ConcurrentHashMap<>();

    @StartupHook
    public void start(ServiceContext context){
        Reflections reflections = new Reflections(new MethodAnnotationsScanner(),
                context.getClassLoader());
        Set<Method> actionMethods = reflections.getMethodsAnnotatedWith(AnalyzerMethod.class);
        ANALYZERS.put(context.getName()+context.getVersion(), actionMethods);
    }

    @ShutdownHook
    public void destroy(ServiceContext context){
        ANALYZERS.remove(context.getName()+context.getVersion());
    }


}
