package application.java;

public class KeyTracker {
    // static variable single_instance of type Singleton
    private static KeyTracker single_instance = null;
    private static long qosKey;

    // static method to create instance of Singleton class
    public static KeyTracker getInstance() {
        if (single_instance == null){
            single_instance = new KeyTracker();
            qosKey = 1;
        }

        return single_instance;
    }

    public long getQosKey() {
        return qosKey;
    }

    public long incrementQosKey() {
        qosKey++;
        return qosKey;
    }

}
