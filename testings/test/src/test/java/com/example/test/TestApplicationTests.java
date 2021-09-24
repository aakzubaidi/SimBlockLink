package com.example.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.HTTPServer;

class TestApplicationTests {

	public static HTTPServer httpServer;

	@BeforeAll
	@DisplayName("Bringing Prometheous Exporter up!")
	public static void setup () throws IOException{
		httpServer = new HTTPServer(8000);
		
	}



	@Test
	@DisplayName("Ensure quality requirements are created and stored in the concurrent storage")
	public void testCreateQualityRequirements() {
		Manager manager = new Manager();
		QoS qos;
		for (int i = 0; i < 8; i++) { 
			if (i % 2 == 0) {
				qos = new QoS(Integer.toString(i), RequieredLevel.LessThan, 2, Unit.s);
				manager.createQos(qos);
			} else {
				qos = new QoS(Integer.toString(i), RequieredLevel.GraterThan, 2, Unit.s);
				manager.createQos(qos);
			}
			assertEquals(Integer.toString(i), manager.getQosStore().get(qos.getQosID()).getQosName(),
			" Should be Equal");
			assertNotEquals(Integer.toString(i), manager.getQosStore().get(qos.getQosID()).getQosName() + "a",
			"Should Not be Equal!");
		}

	}


	@Test
	@DisplayName("Testing Agents Ability to identify breaches and update metrics on the concurrent Storage")
	public void testAgentBreachEvaluation() throws Exception {
		Manager manager = new Manager();
		QoS qos = new QoS("Latency", RequieredLevel.LessThan, 1, Unit.s);
		manager.createQos(qos);
		Agent agent = new Agent(manager, qos);
		agent.evaluateGeneratedMetric(2);
		assertEquals(1, manager.getQosStore().get(qos.getQosID()).getBreachCount(), "breach count should be 1");
		assertNotEquals(1, manager.getQosStore().get(qos.getQosID()).getCompliantCount(), "Compliant count should not be 1");
	}

	@Test
	@DisplayName("Testing Agents Ability to identify compliant and update metrics on the concurrent Storage")
	public void testAgentCompliantEvaluation() throws Exception {
		Manager manager = new Manager();
		QoS qos = new QoS("Throughput", RequieredLevel.GraterThan, 100, Unit.s);
		manager.createQos(qos);
		Agent agent = new Agent(manager, qos);
		agent.evaluateGeneratedMetric(101);
		assertEquals(1, manager.getQosStore().get(qos.getQosID()).getCompliantCount(), "Compliant count should be 1");
		assertNotEquals(1, manager.getQosStore().get(qos.getQosID()).getBreachCount(), "breach count should not be 1");
	}


	@Test
	@DisplayName("Testing Agents Ability to identify breaches and update metrics on the concurrent Storage")
	public void testWorkerScheduling() throws Exception {
		Manager manager = new Manager();
		QoS qos = new QoS("Latency", RequieredLevel.LessThan, 1, Unit.s);
		manager.createQos(qos);

		// schedule workers
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);														
        scheduler.scheduleAtFixedRate(new Worker(manager, qos), 2 , 5, TimeUnit.SECONDS);

		Counter BreachesCounter = Counter.build().name("breach_count").help("This counter tracks the count of metrics that in violation").register();


		Agent agent = new Agent(manager, qos);
		for (int i = 1; i <= 100; i++) {
            agent.evaluateGeneratedMetric(2);
            // rate of metrics reporting to the duration that takes the scheduler to report
            // incidents
			BreachesCounter.inc();
        	TimeUnit.SECONDS.sleep(1);
        }

		// this is just for terminating the workers at hand! uncomment if you want to enforce termination!
		//scheduler.awaitTermination(10, TimeUnit.SECONDS);
		
	
		//assertEquals(1, manager.getQosStore().get(qos.getQosID()).getBreachCount(), "breach count should be 1");
		//assertNotEquals(1, manager.getQosStore().get(qos.getQosID()).getCompliantCount(), "Compliant count should not be 1");

		TimeUnit.SECONDS.sleep(200);
	}

	public static void ckeanUP (){
		httpServer.close();
		
	}




}
