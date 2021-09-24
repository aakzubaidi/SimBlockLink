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
                submittedCompliant = qosStore.get(qosID).getCompliantCount();
                System.out.println(Thread.currentThread().getName() + ": current QoS status: Compliant count: "
                        + submittedCompliant + "|| Breach count: " + submittedBreaches);
                // violationRepoter.submitTransaction(qosID, submittedBreaches,
                // submittedCompliant);
                String[] payload = new String[] { keyTracker.getMetricKey(), qosID, String.valueOf(submittedCompliant),
                        String.valueOf(submittedBreaches) };
                String[] transactionStatus = manager.reportMetrics(contract, method, payload);
                if (transactionStatus[0].equals("success")) {
                    KeyTracker.addTotalSubmittedBreaches(submittedBreaches);
                    KeyTracker.addTotalSubmittedCompliant(submittedCompliant);
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

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}