package co.aikar.timings;

public class ThreadAssertion {
    private static boolean mainThread;
    
    public static boolean isMainThread() {
        return mainThread;
    }
    
    static boolean setMainThread(boolean is) {
        return mainThread = is;
    }
}