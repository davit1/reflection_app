package Executor;


import com.revinate.guava.util.concurrent.RateLimiter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Executor {

    private final MethodFinder methodFinder;
    private List<ExecutorService> threadPools = new ArrayList<ExecutorService>();


    public Executor() {
        this.methodFinder = new MethodFinder();
    }

    private void runAThreadPoolWithThreads(Method method, Object object) {

        int amountOfThreads = method.getAnnotation(Task.class).threads();
        ExecutorService executorService = Executors.newFixedThreadPool(amountOfThreads);
        for (int i = 0; i < amountOfThreads; i++) {
            runThreadWithMethod(method, object, executorService);
        }
        threadPools.add(executorService);

    }

    private void runRateLimiter(Method method, Object object) {

        float numberOfMethodsPerSecond = method.getAnnotation(Task.class).rate();
        ExecutorService executorService = Executors.newCachedThreadPool();
        long end = System.currentTimeMillis() + 60000;
        RateLimiter rateLimiter = RateLimiter.create(numberOfMethodsPerSecond);
        while (System.currentTimeMillis() < end) {
            rateLimiter.acquire(1);

            runThreadWithMethod(method, object, executorService);
        }
        threadPools.add(executorService);
    }

    private void runThreadWithMethod(Method method, Object object, ExecutorService executorService) {

        executorService.execute(() -> {
            Reporter reporter = new Reporter();
            reporter.setMethodName(method.getName());
            long starttime = System.currentTimeMillis();

            boolean isRunning = !Thread.currentThread().isInterrupted();
            try {
                while(isRunning) {
                    method.invoke(object);
                    isRunning = false;
                }
                reporter.wasAvailable();
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println("Either you got no access to invoke the method or " +
                        "the invokation went wrong or the thread was interrupted");
                isRunning = false;
            } finally {
                reporter.setLatency((System.currentTimeMillis() - starttime));
                System.out.println(reporter);
            }
        });

    }

    private void runReflectedMethods(List<Method> methodList, Object object) {
        for (Method method : methodList) {
            if (method.getAnnotation(Task.class).threads() > 0) {
                runAThreadPoolWithThreads(method, object);
            } else {
                runRateLimiter(method, object);
            }
        }
    }

    private void shutdownExecutors() {
        for (ExecutorService exec: threadPools) {
            exec.shutdown();
            if (!exec.isTerminated()) {
                exec.shutdownNow();
            }
        }
    }

    public void execute(Class<MyClass> klass) {
        try {
            Object obj = klass.getConstructor().newInstance();
            List<Method> methodList = methodFinder.getMethodList(klass);
            runReflectedMethods(methodList, obj);
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException | InstantiationException e) {
            System.out.println("Either Interrupted or some shit happende while instantiating class Object");
        }
        shutdownExecutors();
    }
}