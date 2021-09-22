package com.example.test;

import java.util.concurrent.ConcurrentHashMap;

public class Manager {
    private LocalStorage localstorage;



    public Manager() {

        localstorage = LocalStorage.getInstance();


    }





    public String createQos (QoS qos)
    {

			System.out.println("Create Quality Requirement");

         //add this Qos metric to a golable array list to be discoverable by schesuled reporters
		localstorage.getQosStore().put(qos.getQosID(), qos);

        return " Created :"+ qos.getQosID();
        

    }

    
    public ConcurrentHashMap<String, QoS> getQosStore() {
        return localstorage.getQosStore();
	}


    public void printQosStatus(String threadName, String threadID, QoS qos) {
        System.out.println("## Reported incident records about: (" + qos.getQosName() + ") from "+ threadName + " with ID: "
        + threadID +"\n"+
        "----> Current Breaches: " + qos.getBreachCount()+"\n" +
        "----> current Compliant Logs: " + qos.getCompliantCount());

    }


    
}
