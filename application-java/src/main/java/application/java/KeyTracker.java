package application.java;

public class KeyTracker {
    // static variable single_instance of type Singleton
    private static KeyTracker single_instance = null;
    private static long qosKey;
    private static long metricKey;

    // static method to create instance of Singleton class
    public static KeyTracker getInstance() {
        if (single_instance == null){
            single_instance = new KeyTracker();
            qosKey = 22;
            metricKey = 0;
        }

        return single_instance;
    }

    public String getQosKey() {
        qosKey++;
        return "Q".concat(Long.toString(qosKey));
    }

    public String getMetricKey() {
        metricKey++;
        return "M".concat(Long.toString(metricKey));
    }




}
