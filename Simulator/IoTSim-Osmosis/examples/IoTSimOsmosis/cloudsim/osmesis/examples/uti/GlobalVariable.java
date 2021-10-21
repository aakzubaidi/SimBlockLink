package IoTSimOsmosis.cloudsim.osmesis.examples.uti;

import java.util.ArrayList;

import application.java.Manager;
import application.java.QoS;

public class GlobalVariable {

	    // static variable single_instance of type Singleton
	    private static GlobalVariable single_instance = null;
	    private static Manager manager;
	    private static ArrayList<QoS> qos;
	    

	    // static method to create instance of Singleton class
	    public static GlobalVariable getInstance() {
	        if (single_instance == null){
	            single_instance = new GlobalVariable();
	            qos = new ArrayList <QoS>();
	        }

	        return single_instance;
	    }
	    
	   
	    
	    public static Manager getManager() {
			return manager;
		}



		public static void setManager(Manager manager) {
			GlobalVariable.manager = manager;
		}



		public static void addQoS (QoS q)
	    {
	    	qos.add(q);
	    }

	    public static ArrayList<QoS> getQoS ()
	    {
	    	
	    	return qos;
	    }
	    

	}




