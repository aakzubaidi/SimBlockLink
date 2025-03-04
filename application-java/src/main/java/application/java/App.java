/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Contract;

public class App {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(String[] args) throws Exception {
		ConnectionProfile connectionProfile = new ConnectionProfile();
		connectionProfile.setPemFileLocation("../../fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
		connectionProfile.setCaClientURL("https://localhost:7054");
		connectionProfile.setAdminIdentity("admin");
		connectionProfile.setAdminSecret("adminpw");
		connectionProfile.setClientIdentity("AliAlzubaidi3");
		//how many times to attemp resubmitting failed transactions
		int maxRetry = 5;
		//Do you wish to consider all transactions' resubmission attempts as one failed transaction?
		boolean countfailedAttampt = false;
		
		Manager manager = new Manager(connectionProfile,maxRetry, countfailedAttampt);
		manager.generateIdentity();
		//manager.createMetricExporter(8004);









		/**
		 * helper function for getting connected to the gateway
		 * 
		 * @throws IOException
		 */
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "fabric-samples", "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

		// Configure the gateway connection used to access the network.
		Gateway.Builder builder = Gateway.createBuilder().identity(wallet, connectionProfile.getClientIdentity()).networkConfig(networkConfigPath);

		// Create a gateway connection
		Gateway gateway = builder.connect();
		Network network = gateway.getNetwork("mychannel");
		Contract contract = network.getContract("chaincode");


		//define quality requirement
		//provide (contract, quality metric name, required level, threshold)
		//example (contract, method, latency, LessThan, 2, s)
		QoS qos = new QoS( "latency", RequieredLevel.LessThan, 1,  Unit.s);
		manager.createQos(contract, "presistQoS",qos);
		
		// schedule workers
        manager.assignQosToWorker(manager, contract, "reportMetric", qos, 2, 3);


		Agent latencyAgent = new Agent(manager, qos) ;

		double TransmissionTime;
        for (int i = 1; i <= 1000; i++) {

			if (i % 2 == 0)
			{
				TransmissionTime = 0.5;
			}
			else
			{
				TransmissionTime = 2;
			}

            latencyAgent.evaluateGeneratedMetric(TransmissionTime);
            // rate of metrics reporting to the duration that takes the scheduler to report
            // incidents
        TimeUnit.MILLISECONDS.sleep(100);
        }

		TimeUnit.SECONDS.sleep(40);
		KeyTracker keyTracker = KeyTracker.getInstance();
		System.out.println("Submitted Breaches cases: "+ keyTracker.getTotalSubmittedBreaches());
		System.out.println("Submitted Compliant cases: "+ keyTracker.getTotalSubmittedCompliant());
		System.out.println("current Stored Breaches: "+ manager.getQosStore().get(qos.getQosID()).getBreachCount());
		System.out.println("current Stored Compliant: "+ manager.getQosStore().get(qos.getQosID()).getCompliantCount());



		


	}
}
