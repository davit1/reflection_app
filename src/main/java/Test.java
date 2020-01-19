import Executor.Executor;
import Executor.MyClass;

public class Test {

    public static void main(String[] args) {
        Executor executor = new Executor();
        executor.execute(MyClass.class);
    }

}