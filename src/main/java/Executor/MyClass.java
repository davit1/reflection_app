package Executor;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyClass {

    private static int counter = 0;


    @Task(rate = 5)
    public void method1() {
        try {
            int i = counter++;
            System.out.println("method " + i);
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500));
            System.out.println("Done!");
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }

    }


    @Task(threads = 3)
    public void method2() throws InterruptedException {
        System.out.println("method 2 number " + counter++);
        TimeUnit.SECONDS.sleep(5);
        System.out.println("method 2 done!");
    }


}