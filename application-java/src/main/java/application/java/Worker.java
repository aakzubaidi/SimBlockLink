package application.java;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.hyperledger.fabric.gateway.Contract;
/**
 * @apiNote This for scheduling the violation reporting service until the
 *          monitoried app is back to normal or timeout is reached
 * @author Ali Alzubaidi
 * @since 2021
 * @version 0.0.1
 */

public class Worker extends TimerTask {
    private Manager manager;
    private KeyTracker keyTracker;
    private Contract contract;
    private String method;
    private int submittedBreaches;
    private int submittedCompliant;
    private String qosID;
    private QoS qos;
    ConcurrentHashMap<String, QoS> qosStore;

    public Worker(Manager manager, Contract contract, String method, QoS qos) {
        this.keyTracker = KeyTracker.getInstance();
        this.manager = manager;
        this.contract = contract;
        this.method = method;
        this.submittedBreaches = 0;
        this.submittedCompliant = 0;
        this.qosID = qos.getQosID();
        this.qosStore = manager.getQosStore();


    }

    @Override
    public void run() {
        System.out.println("I am  thread whose name is: " + Thread.currentThread().getName() + " with ID: "
                + Thread.currentThread().getId() + " --- checking for any identified incident about("+qosID+")");
            // for testing purposes, print current incidents records for this QoS metric
            manager.printQosStatus(Thread.currentThread().getName(), String.valueOf(Thread.currentThread().getId()), qos);

        if (qosStore.get(qosID).getBreachCount() > 0) {
            try {
                System.out.println(Thread.currentThread().getName() + " with ID: "
                + Thread.currentThread().getId() +
                        " has identified breaches about("+qosID+"). it is now reporting them to the blockchain");
                submittedBreaches = qosStore.get(qosID).getBreachCount();
                submittedCompliant = qosStore.get(qosID).getCompliantCount();
                //violationRepoter.submitTransaction(qosID, submittedBreaches, submittedCompliant);
                String [] payload = new String [] {keyTracker.getMetricKey(), qosID, String.valueOf(submittedCompliant), String.valueOf(submittedBreaches)};
                manager.reportMetrics(contract, method, payload);

            } catch (Exception e) {
                e.printStackTrace();
            }
            int deltaBreaches = qosStore.get(qosID).getBreachCount() - submittedBreaches;
            System.out.println( "deltaBreaches = "+ deltaBreaches);
            if (deltaBreaches >= 0) {
                qosStore.get(qosID).setBreachCount(deltaBreaches);
            } else {
                qosStore.get(qosID).setBreachCount(0);

            }

            int deltaCompliant = qosStore.get(qosID).getCompliantCount() - submittedCompliant;
            System.out.println( "deltaCompliant = "+ deltaCompliant);
            if (deltaCompliant >= 0) {
                qosStore.get(qosID).setCompliantCount(deltaCompliant);
            } else {
                qosStore.get(qosID).setCompliantCount(0);

            }
            System.out.println(
                    Thread.currentThread().getName() + "  has sucessfully reported the incident to blockchain");
        }

    }

}