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

		LocalStorage localstorage = LocalStorage.getInstance();
        // choose a an identity name for the simulator side.
        localstorage.setmanagerIdentity("aliAlzubaidi1");
        // name the QoS metric (throughput, latency, etc)
        String qosName = ("testEclipse32");
        // Requered Service Level (GraterThan, LessThan, Equals)
        RequieredLevel requieredLevel = RequieredLevel.LessThan;
        // set a threshold to test against
        double threshold = 10;

		//define quality requirement
		QoS qos = new QoS(qosName, requieredLevel, threshold);



		// enrolls the admin and registers the user
		try {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

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
		Gateway.Builder builder = Gateway.createBuilder().identity(wallet, "alo").networkConfig(networkConfigPath);

		// Create a gateway connection
		Gateway gateway = builder.connect();
		Network network = gateway.getNetwork("mychannel");
		Contract contract = network.getContract("chaincode");

		String[] payload = new String[] { qos.getQosID(), qos.getQosName(), qos.getLevel().toString(), Double.toString(qos.getThreshold())};

		System.out.println(qos.getQosID());
		System.out.println(qos.getQosName());
		System.out.println(qos.getLevel().toString());
		System.out.println(Double.toString(qos.getThreshold()));

		BlockchainAPI api = new BlockchainAPI();
		byte[] result = null;

		try {
			// create qyality requirment
			System.out.println("Create Quality Requirement");
			result = api.submitTransaction(contract, "presistQoS", payload);
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}


		payload = new String[] {"0001", "1", "1", "1"};
		try {
			// create qyality requirment
			System.out.println("report metric");
			result = api.submitTransaction(contract, "reportMetric", payload);
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}

	}
}
