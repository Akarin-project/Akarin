package co.aikar.timings;

public class ThreadAssertion {
    private static boolean mainThread;
    
    public static boolean is() {
        return mainThread;
    }
    
    static void start() {
        mainThread = true;
    }
    
    public static void close() {
        mainThread = false;
    }
}
