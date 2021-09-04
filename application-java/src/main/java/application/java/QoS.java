package application.java;

/**
  * @apiNote // Requered Service Level (GraterThan, LessThan, Equals)
  * @author Ali Alzubaidi
  */
  enum RequieredLevel {

    GraterThan, LessThan, Equals
}

public class QoS {

    //Id of Quality requirement
    private long qosID;
    //name of quality requirement
    private String qosName;
    // Requered Service Level (GraterThan, LessThan, Equals)
    private RequieredLevel level;
    // set a threshold to test against
    private double threshold = 10;
    // count of current compliant logs, up to where we find a set of breaches
    private int compliantLogs;
    // count of current breaches
    private int Breaches;

    public QoS (String qosName, RequieredLevel level, double threshold){

        this.qosName = qosName;
        this.level = level;
        this.threshold = threshold;

        KeyTracker keyTracker = KeyTracker.getInstance();
        this.qosID = keyTracker.incrementQosKey();
    }

    
    public String getQosID() {
        return Long.toString(qosID);
    }


    public String getQosName() {
        return qosName;
    }


    public RequieredLevel getLevel() {
        return level;
    }

    public double getThreshold() {
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


}