package com.example.test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestApplicationTests {

	@BeforeEach 
	@DisplayName("Ensure quality requirements are created and stored in the concurrent storage")                                     
    public void testCreateQualityRequirements() {
		Manager manager = new Manager();

		for (int i = 0; i < 8; i++) {
			QoS qos = new QoS(Integer.toString(i), RequieredLevel.LessThan, 2,  Unit.s);
			manager.createQos(qos);

			assertEquals(Integer.toString(i), manager.getQosStore().get(qos.getQosID()).getQosName(), " Should be Equal");
			assertNotEquals(Integer.toString(i), manager.getQosStore().get(qos.getQosID()).getQosName()+"a", "Should Not be Equal!");		
		}
		
    }

	



}
