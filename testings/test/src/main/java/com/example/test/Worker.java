package com.example.test;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import io.prometheus.client.Counter;

public class Worker extends TimerTask{


    private int submittedBreaches;
    private int submittedCompliant;
    private String qosID;
    ConcurrentHashMap<String, QoS> qosStore;
    private Counter workerCounter;
    public Worker(Manager manager, QoS qos) {
        this.submittedBreaches = 0;
        this.submittedCompliant = 0;
        this.qosID = qos.getQosID();
        this.qosStore = manager.getQosStore();
        this.workerCounter = Counter.build().name("_handeled_metrics").help("This counter tracks the count of breach metrics handeled by the worker").register();

        System.out.println("Done consutructing");

    }

    @Override
    public void run() {

        if (qosStore.get(qosID).getBreachCount() > 0 || qosStore.get(qosID).getCompliantCount() > 0) {
            try {
                // System.out.println(Thread.currentThread().getName() + " with ID: "
                // + Thread.currentThread().getId() +
                // " has identified breaches about("+qosID+"). it is now reporting them to the
                // blockchain");
                submittedBreaches = qosStore.get(qosID).getBreachCount();
                workerCounter.inc(submittedBreaches);
                submittedCompliant = qosStore.get(qosID).getCompliantCount();
                System.out.println(Thread.currentThread().getName() + ": current QoS status: Compliant count: "
                        + submittedCompliant + "|| Breach count: " + submittedBreaches);
                // violationRepoter.submitTransaction(qosID, submittedBreaches,
                // submittedCompliant);
                int deltaBreaches = qosStore.get(qosID).getBreachCount() - submittedBreaches;
                if (deltaBreaches >= 0) {
                    qosStore.get(qosID).setBreachCount(deltaBreaches);
                } else {
                    qosStore.get(qosID).setBreachCount(0);

                }

                int deltaCompliant = qosStore.get(qosID).getCompliantCount() - submittedCompliant;
                if (deltaCompliant >= 0) {
                    qosStore.get(qosID).setCompliantCount(deltaCompliant);
                } else {
                    qosStore.get(qosID).setCompliantCount(0);

                }
                System.out.println(Thread.currentThread().getName()
                        + ": Subcessfully reported metrics to blockchain \n current QoS Status: Compliant count: "
                        + deltaCompliant + "|| Breach count: " + deltaBreaches);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    
}
