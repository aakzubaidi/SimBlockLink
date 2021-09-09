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

		int attempt = 0;
		int maxRetry = 5;
		
		byte[] result = null;
		String readableResult = null;

		// assume the transaction will fail
        String TransactionStatus = "fail";

		// try submitting until otherwise (until transaction succeeds)
        while (attempt < maxRetry && TransactionStatus.equals("fail")) {
			try{
				System.out.println("Attempt "+ attempt+ ": submitting Transaction to contract: ("+ contract +") to invoke method: ("+ smartContractMethod+")");
				result = contract.createTransaction("presistQoS")
				.submit(payload);
				readableResult = new String(result, StandardCharsets.UTF_8);
				TransactionStatus = "success";
			} catch (ContractException | InterruptedException e) {
				e.printStackTrace();
			}
			attempt++;
		}		

		return readableResult;

	}

}
