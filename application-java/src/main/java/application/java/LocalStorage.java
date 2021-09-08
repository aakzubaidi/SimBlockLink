package application.java;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @apiNote a singleton data store to be commenly accessed by monitoring tool
 *          and blockchain helpers.
 * @author Ali Alzubaidi
 * @date 2021
 */


// Java program implementing Singleton class
// with getInstance() method
public class LocalStorage {
    // static variable single_instance of type Singleton
    private static LocalStorage single_instance = null;
    // store for incidents (QoSmetric, properties)
    public ConcurrentHashMap <String,QoS> qosStore = null;

    // waitng time for the Violation Reporter. In seconds
    private static int delay;

    // private constructor restricted to this class itself
    private LocalStorage() {
        delay = 10;
        qosStore = new ConcurrentHashMap <String,QoS>();
    }

    // static method to create instance of Singleton class
    public static LocalStorage getInstance() {
        if (single_instance == null)
            single_instance = new LocalStorage();

        return single_instance;
    }


    public static int getDelay() {
        return delay;
    }

    public static void setDelay(int delay) {
        LocalStorage.delay = delay;
    }

	public ConcurrentHashMap<String, QoS> getQosStore() {
		return qosStore;
	}

}