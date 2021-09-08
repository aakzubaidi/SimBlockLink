package application.java;

import java.net.MalformedURLException;

/**
 * @apiNote This for communicating and reporting violation to the blockchain
 * @author Ali Alzubaidi
 * @since 2021
 * @version 0.0.1
 */


public class Agent {


	public Agent() throws MalformedURLException {

	}

	public void evaluateGeneratedMetric(QoS qos, double generatedMetric) throws Exception {
		// every time you have a reading about a metric, evaluate.


		// if there is a breach, then
		if (qos.getLevel().equals(RequieredLevel.GraterThan.toString())) {

			if (generatedMetric < Double.valueOf(qos.getThreshold())) {
				//System.out.println("---- A Breach observed!!!------> ");
				qos.setBreaches(qos.getBreaches() + 1);
			} else {

				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qos.setCompliantLogs(qos.getCompliantLogs() + 1);

			}

		} else if (qos.getLevel().equals(RequieredLevel.LessThan.toString())) {

			if (generatedMetric > Double.valueOf(qos.getThreshold())) {
				//System.out.println("---- A Breach observed!!!------> ");
				qos.setBreaches(qos.getBreaches() + 1);
			} else {


				//System.out.println(
						//"---- Compliant and all good! :-) ---- no need to report to blockchain --->");
				qos.setCompliantLogs(qos.getCompliantLogs() + 1);
			}

		} else if (qos.getLevel().equals(RequieredLevel.Equals.toString())) {

			if (generatedMetric != Double.valueOf(qos.getThreshold())) {
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