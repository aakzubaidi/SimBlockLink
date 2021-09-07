package application.java;

import java.net.MalformedURLException;

/**
 * @apiNote This for communicating and reporting violation to the blockchain
 * @author Ali Alzubaidi
 * @since 2021
 * @version 0.0.1
 */


public class Agent {
	private static LocalStorage localStorage;
	private static QoS qos;


	public Agent() throws MalformedURLException {
		localStorage = LocalStorage.getInstance();
	}

	public void evaluateMonitoringMetric(String QoSmetric, double monitoringReading, RequieredLevel level,
			double threshold) throws Exception {
		// every time you have a reading about throghput, evaluate.

		//get incident record of this QoS metric
		qos = localStorage.getQosStore().get(QoSmetric);

		// if there is a breach, then
		if (level.equals(RequieredLevel.GraterThan)) {

			if (monitoringReading < threshold) {
				//System.out.println("---- A Breach observed!!!------> ");
				qos.setBreaches(qos.getBreaches() + 1);
			} else {

				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qos.setCompliantLogs(qos.getCompliantLogs() + 1);

			}

		} else if (level.equals(RequieredLevel.LessThan)) {

			if (monitoringReading > threshold) {
				//System.out.println("---- A Breach observed!!!------> ");
				qos.setBreaches(qos.getBreaches() + 1);
			} else {


				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qos.setCompliantLogs(qos.getCompliantLogs() + 1);
			}

		} else if (level.equals(RequieredLevel.LessThan)) {

			if (monitoringReading != threshold) {
				//System.out.println("---- A Breach observed!!!------> ");
				qos.setBreaches(qos.getBreaches() + 1);
			} else {


				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qos.setCompliantLogs(qos.getCompliantLogs() + 1);

			}

		} else {
			System.out.println("The Requiered QoS level is not correctly defined.");
		}
	}

}