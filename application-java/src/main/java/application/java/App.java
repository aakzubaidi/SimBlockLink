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
		Manager manager = new Manager();
		String result = manager.generateIdentity("AliAlzubaidi");
		System.out.println(result);








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
		Gateway.Builder builder = Gateway.createBuilder().identity(wallet, manager.getIdentity()).networkConfig(networkConfigPath);

		// Create a gateway connection
		Gateway gateway = builder.connect();
		Network network = gateway.getNetwork("mychannel");
		Contract contract = network.getContract("chaincode");


		//define quality requirement
		//provide (contract, quality metric name, required level, threshold)
		//example (contract, method, latency, LessThan, 2, s)
		QoS qos = new QoS(contract, "presistQoS", "latency", RequieredLevel.LessThan, 2,  Unit.s);
		String qosdata = manager.addQosToConcurrentStorage(qos);
		System.out.println(qosdata);
		
		BlockchainAPI api = new BlockchainAPI();

		String [] payload = new String[] {"0001", "1", "1", "1"};
		try {
			// create qyality requirment
			System.out.println("report metric");
			result = api.submitTransaction(contract, "reportMetric", payload);
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}

	}
}
