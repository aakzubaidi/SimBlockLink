package org.example;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.Genson;

@DataType()
public class Metric {

    private final static Genson genson = new Genson();

    @Property()
    private String qosID;

    @Property()
    private String compliantCount;

    @Property()
    private String breachCount;


    public String getQosID() {
        return qosID;
    }

    public void setQosID(String qosID) {
        this.qosID = qosID;
    }

    public String getCompliantCount() {
        return compliantCount;
    }

    public void setCompliantCount(String compliantCount) {
        this.compliantCount = compliantCount;
    }

    public String getBreachCount() {
        return breachCount;
    }

    public void setBreachCount(String breachCount) {
        this.breachCount = breachCount;
    }

    public String toJSONString() {
        return genson.serialize(this).toString();
    }

    public static Metric fromJSONString(String json) {
        Metric metric = genson.deserialize(json, Metric.class);
        return metric;
    }


    
}
