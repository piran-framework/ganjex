import com.piranframework.ganjex.api.Ganjex;
import com.piranframework.ganjex.api.GanjexConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, InterruptedException {
        Ganjex.run(new GanjexConfiguration.Builder()
                .libPath("PATH_TO_GANJEX_LIBS")
                .servicePath("PATH_TO_GANJEX_SERIVCES")
                .watcherDelay(1)
                .hooks(new TwitterHookContainer())
                .build());

        startAnalyzerCollector();
    }

    private static void startAnalyzerCollector() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    List<String> analyzedTweets = new ArrayList<>();
                    Collection<Set<Method>> methodsCollection = TwitterHookContainer.ANALYZERS.values();
                    for (Set<Method> methods : methodsCollection) {
                        for (Method m : methods) {
                            analyzedTweets.addAll((List<String>) m.invoke(m.getDeclaringClass().getConstructor().newInstance()));
                        }
                    }
                    for (String analyzedTweet : analyzedTweets) {
                        System.out.println(analyzedTweet);
                    }
                } catch (InterruptedException
                        | NoSuchMethodException
                        | InstantiationException
                        | InvocationTargetException
                        | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
