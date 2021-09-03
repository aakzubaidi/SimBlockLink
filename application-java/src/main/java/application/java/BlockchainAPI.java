/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

public class BlockchainAPI {



	// helper function for getting connected to the gateway
	public byte[] submitTransaction(Contract contract, String smartContractMethod, String[] payload) throws Exception {

		byte[] result = null;

		// System.out.println("Submit Transaction: InitLedger creates the initial set of
		// assets on the ledger.");
		// contract.submitTransaction("InitLedger");

		try{
			System.out.println("\n");

			System.out.println("Create Quality Requirement");
			result = contract.createTransaction("presistQoS")
			.submit(payload);
		} catch (ContractException | InterruptedException e) {
			e.printStackTrace();
		}

		return result;

	}

}
