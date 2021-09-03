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
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class Test {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	private static Gateway gateway;
	private static Network network;
	private static Contract contract;

	public static void main(String[] args) throws Exception {
		// enrolls the admin and registers the user
		try {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder().identity(wallet, "alo").networkConfig(networkConfigPath)
				.discovery(true);
		gateway = builder.connect();
		// get the network and contract
		network = gateway.getNetwork("mychannel");
		contract = network.getContract("chaincode");

		byte[] result = null;

		System.out.println("\n");
		try {
			System.out.println("Create Quality Requirement");
			// Non existing asset asset70 should throw Error
			result = contract.submitTransaction("presistQoS", "asset7227", "blue", "5", "Tomoko");
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}
	}
}
