/**
 * @author @aakzubaidi Ali Alzubaidi
 * 
 * 
 */


package application.java;

import java.nio.charset.StandardCharsets;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

public class BlockchainAPI {


	/**
	 * helper function for submitting transactions to the blockchain side
	 * @param contract
	 * @param smartContractMethod
	 * @param payload
	 * @return
	 * @throws Exception
	 */
	public String submitTransaction(Contract contract, String smartContractMethod, String[] payload) throws Exception {

		byte[] result = null;
		String readableResult = null;

		// System.out.println("Submit Transaction: InitLedger creates the initial set of
		// assets on the ledger.");
		// contract.submitTransaction("InitLedger");

		try{

			System.out.println("submitting Transaction to contract: ("+ contract +") to invoke method: ("+ smartContractMethod+")");
			result = contract.createTransaction("presistQoS")
			.submit(payload);
		} catch (ContractException | InterruptedException e) {
			e.printStackTrace();
		}

		readableResult = new String(result, StandardCharsets.UTF_8);

		return readableResult;

	}

}
