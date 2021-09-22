package com.example.test;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

public class Agent {

    private Manager manager;
	private String qosID;
    private QoS qos;
    ConcurrentHashMap<String, QoS> qosStore;


	public Agent(Manager manager, QoS qos) throws MalformedURLException {
		this.manager = manager;
		this.qosID = qos.getQosID();
        this.qosStore = this.manager.getQosStore();
		this.qos = qos;
	}

	public void evaluateGeneratedMetric(double generatedMetric) throws Exception {
		// every time you have a reading about a metric, evaluate.


		// if there is a breach, then
		if (qos.getLevel().equals(RequieredLevel.GraterThan.toString())) {

			if (generatedMetric < Double.valueOf(qos.getThreshold())) {
				//System.out.println("---- A Breach observed!!!------> ");
				qosStore.get(qosID).setBreachCount(qos.getBreachCount() + 1);
			} else {

				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qosStore.get(qosID).setCompliantCount(qos.getCompliantCount()+1);

			}

		} else if (qos.getLevel().equals(RequieredLevel.LessThan.toString())) {

			if (generatedMetric > Double.valueOf(qos.getThreshold())) {
				//System.out.println("---- A Breach observed!!!------> ");
				qosStore.get(qosID).setBreachCount(qos.getBreachCount() + 1);
			} else {


				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qosStore.get(qosID).setCompliantCount(qos.getCompliantCount() + 1);
			}

		} else if (qos.getLevel().equals(RequieredLevel.Equals.toString())) {

			if (generatedMetric != Double.valueOf(qos.getThreshold())) {
				//System.out.println("---- A Breach observed!!!------> ");
				qosStore.get(qosID).setBreachCount(qos.getBreachCount() + 1);
			} else {


				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qosStore.get(qosID).setCompliantCount(qos.getCompliantCount() + 1);

			}

		} else {
			System.out.println("The Requiered QoS level is not correctly defined.");
		}
	}
    
}
