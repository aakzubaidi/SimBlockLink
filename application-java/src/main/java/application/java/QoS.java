package application.java;

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
    private int compliantCount;
    // count of current breaches
    private int breachCount;
    //the name of the contract


    /**
     * 
     * @param contract smart contract name
     * @param method smart contract's method name
     * @param qosName quality requirement name
     * @param level required level: ex RequieredLevel.LessThan, GraterThan, Equals
     * @param threshold value of the quality requirement 
     * @param unit unit of the value (ms, s, m, h)
     */
    public QoS (String qosName, RequieredLevel level, double threshold, Unit unit){

        KeyTracker keyTracker = KeyTracker.getInstance();
        this.qosID = keyTracker.getQosKey();
        this.qosName = qosName;
        this.level = level.toString();
        this.threshold = String.valueOf(threshold);
     
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


    public int getCompliantCount() {
        return compliantCount;
    }

    public void setCompliantCount(int compliantCount) {
        this.compliantCount = compliantCount;
    }

    public int getBreachCount() {
        return breachCount;
    }

    public void setBreachCount(int breachCount) {
        this.breachCount = breachCount;
    }


    


}