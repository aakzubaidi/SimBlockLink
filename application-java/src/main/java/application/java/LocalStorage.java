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


    // private constructor restricted to this class itself
    private LocalStorage() {
        qosStore = new ConcurrentHashMap <String,QoS>();
    }

    // static method to create instance of Singleton class
    public static LocalStorage getInstance() {
        if (single_instance == null)
            single_instance = new LocalStorage();

        return single_instance;
    }

	public ConcurrentHashMap<String, QoS> getQosStore() {
		return qosStore;
	}

}