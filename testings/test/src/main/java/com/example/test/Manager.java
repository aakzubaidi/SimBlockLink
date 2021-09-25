package com.example.test;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Manager {
    private LocalStorage localstorage;
    private ArrayList <QoS> assignedQoS;



    public Manager() {

        this.localstorage = LocalStorage.getInstance();
        this.assignedQoS = new ArrayList<QoS>(); 


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



    public String assignQosToWorker (Manager manager, QoS qos, long intialDelay, long period)
    {
        String result = "fail: already assigned";

        if (!assignedQoS.contains(qos))
        {
        // schedule workers
        Worker worker = new Worker(manager, qos );
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);														
        scheduler.scheduleAtFixedRate(worker, intialDelay , period, TimeUnit.SECONDS);
        result = "success: Assigned to a new worker";
        }

        return result;
    }


    public void printQosStatus(String threadName, String threadID, QoS qos) {
        System.out.println("## Reported incident records about: (" + qos.getQosName() + ") from "+ threadName + " with ID: "
        + threadID +"\n"+
        "----> Current Breaches: " + qos.getBreachCount()+"\n" +
        "----> current Compliant Logs: " + qos.getCompliantCount());

    }




    
}
