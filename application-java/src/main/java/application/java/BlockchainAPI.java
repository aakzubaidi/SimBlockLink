/**
 * @author @aakzubaidi Ali Alzubaidi
 * 
 * 
 */

package application.java;

import java.nio.charset.StandardCharsets;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public class BlockchainAPI {

	private InstrumentFactory metricsRegistry;
	private int maxRetry;
	private boolean countfailedAttampt;

	public BlockchainAPI(int maxRetry, boolean countfailedAttampt) {
		metricsRegistry = new InstrumentFactory();
		this.maxRetry = maxRetry;
		this.countfailedAttampt = countfailedAttampt;
	}

	/**
	 * helper function for submitting transactions to the blockchain side
	 * 
	 * @param contract
	 * @param smartContractMethod
	 * @param payload
	 * @return
	 * @throws Exception
	 */
	public String submitTransaction(Contract contract, String smartContractMethod, String[] payload) throws Exception {

		int attempt = 0;


		byte[] result = null;
		String readableResult = null;

		// assume the transaction will fail
		String TransactionStatus = "fail";

		Counter[] counters = metricsRegistry.getCounter(smartContractMethod);
		Counter sucessCounter = counters[0];
		Counter failCounter = counters[1];
		Histogram.Timer requestTimer = metricsRegistry.getHistogram(smartContractMethod).startTimer();

		// try submitting until otherwise (until transaction succeeds)
		while (attempt < maxRetry && TransactionStatus.equals("fail")) {
			try {
				System.out.println("Attempt " + attempt + ": submitting Transaction to contract: (" + contract
						+ ") to invoke method: (" + smartContractMethod + ")");
				result = contract.createTransaction(smartContractMethod).submit(payload);
				readableResult = new String(result, StandardCharsets.UTF_8);
				TransactionStatus = "success";
				sucessCounter.inc();
				requestTimer.observeDuration();

			} catch (ContractException | InterruptedException e) {
				e.printStackTrace();
			}
			attempt++;
		}

		if (TransactionStatus.equals("fail")){

			if (countfailedAttampt)
			{
				failCounter.inc();
				
			}else
			{
				failCounter.inc(maxRetry);
			}
		}

		return readableResult;

	}

}
