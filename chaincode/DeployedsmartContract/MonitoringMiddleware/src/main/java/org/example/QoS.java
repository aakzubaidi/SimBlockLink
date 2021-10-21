package org.example;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.Genson;

@DataType()
public class QoS {

    private final static Genson genson = new Genson();

    @Property()
    private String name;
    @Property()
    private String requieredLevel;
    @Property()
    private String value;




    public QoS(){
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toJSONString() {
        return genson.serialize(this).toString();
    }

    public static QoS fromJSONString(String json) {
        QoS qos = genson.deserialize(json, QoS.class);
        return qos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequieredLevel() {
        return requieredLevel;
    }

    public void setReuiredLevel(String reuieredLevel) {
        this.requieredLevel = reuieredLevel;
    }
}
