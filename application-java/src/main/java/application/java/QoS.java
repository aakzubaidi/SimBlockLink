package application.java;

import org.hyperledger.fabric.gateway.Contract;

/**
  * @apiNote Quality requirement model and controller
  * @author Ali Alzubaidi
  */
enum RequieredLevel {

    GraterThan, LessThan, Equals;
}

enum Unit {

    ms, s, m, h;
}

public class QoS {

    //Id of Quality requirement
    private String qosID;
    //name of quality requirement
    private String qosName;
    // Requered Service Level (GraterThan, LessThan, Equals)
    private String level;
    // set a threshold to test against
    private String threshold;
    // count of current compliant logs, up to where we find a set of breaches
    private int compliantLogs;
    // count of current breaches
    private int Breaches;
    //the name of the contract
    private Contract contract;
    // method for creating qos
    private String method;


    /**
     * 
     * @param contract smart contract name
     * @param method smart contract's method name
     * @param qosName quality requirement name
     * @param level required level: ex RequieredLevel.LessThan, GraterThan, Equals
     * @param threshold value of the quality requirement 
     * @param unit unit of the value (ms, s, m, h)
     */
    public QoS (Contract contract, String method, String qosName, RequieredLevel level, double threshold, Unit unit){

        this.contract = contract;
        this.method = method;
        KeyTracker keyTracker = KeyTracker.getInstance();
        this.qosID = "Q".concat(Long.toString(keyTracker.incrementQosKey()));
        this.qosName = qosName;
        this.level = level.toString();
        this.threshold = String.valueOf(threshold);
     

        // Create qos at both sides, the blockchain-side and client-side.
        createQos();
    }

    
    public String getQosID() {
        return qosID;
    }


    public String getQosName() {
        return qosName;
    }


    public String getLevel() {
        return level;
    }

    public String getThreshold() {
        return threshold;
    }


    public int getCompliantLogs() {
        return compliantLogs;
    }

    public void setCompliantLogs(int compliantLogs) {
        this.compliantLogs = compliantLogs;
    }

    public int getBreaches() {
        return Breaches;
    }

    public void setBreaches(int breaches) {
        Breaches = breaches;
    }


    private String createQos ()
    {
        String result = null;
        String[] payload = new String[] {this.qosID, this.qosName, this.level, this.threshold};
        System.out.println(payload);
        BlockchainAPI api = new BlockchainAPI();

        try {
			// create qyality requirment
			System.out.println("Create Quality Requirement");
			result = api.submitTransaction(this.contract, this.method , payload);
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}
        

        System.out.println("Status of creating quality requirement at blockchain side: \n" + result);
        return result;
    }


}