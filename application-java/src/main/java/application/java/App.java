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
import org.hyperledger.fabric.gateway.ContractException;

public class App {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(String[] args) throws Exception {
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

		String[] payload = new String[] { "asset72267485236fa", "blue", "5", "Tomoko" };

		BlockchainAPI api = new BlockchainAPI();
		byte[] result = null;

		// System.out.println("Submit Transaction: InitLedger creates the initial set of
		// assets on the ledger.");
		// contract.submitTransaction("InitLedger");

		System.out.println("\n");
		try {
			System.out.println("Create Quality Requirement");
			// Non existing asset asset70 should throw Error
			result = api.submitTransaction(contract, "presistQoS", payload);
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}

	}
}
