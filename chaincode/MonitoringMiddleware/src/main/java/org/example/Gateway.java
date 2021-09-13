/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import static java.nio.charset.StandardCharsets.UTF_8;

@Contract(name = "chaincode", info = @Info(title = "Gateway contract", description = "My Smart Contract", version = "0.0.1", license = @License(name = "Apache-2.0", url = ""), contact = @Contact(email = "Gateway@example.com", name = "Asset", url = "http://Gateway.me")))
@Default
public class Gateway implements ContractInterface {
    public Gateway() {

    }

    @Transaction()
    public boolean Exists(Context ctx, String AssetId) {
        byte[] buffer = ctx.getStub().getState(AssetId);
        return (buffer != null && buffer.length > 0);
    }

    // add provider
    @Transaction()
    public void presistQoS(Context ctx, String qosID, String name, String requieredLevel, String value) {
        boolean exists = Exists(ctx, qosID);
        if (exists) {
            throw new RuntimeException("ID:  " + qosID + " already exists");
        }
        QoS qos = new QoS();
        qos.setName(name);
        qos.setReuiredLevel(requieredLevel);
        qos.setValue(value);
        ctx.getStub().putState(qosID, qos.toJSONString().getBytes(UTF_8));
    }




    //Enahnced approached for high throughput metrics evaluation
    @Transaction()
    public String reportMetric(Context ctx, String metricID, String qosID, String compliantCount, String breachCount) {
        boolean exists = Exists(ctx, metricID);
        if (exists) {
            throw new RuntimeException("The metric with ID: " + metricID + " Already Exist");
        }
        String mesaage = "";

        Metric metric = new Metric();
        metric.setQosID(qosID);
        metric.setCompliantCount(compliantCount);
        metric.setBreachCount(breachCount);

        
        ctx.getStub().putState(metricID, metric.toJSONString().getBytes(UTF_8));

        exists = Exists(ctx, metricID);
        if (exists) {
            mesaage = "Metric with ID: "+ metricID + "has been sucessfully comitted to the ledger";
        }
        else {
            mesaage = "Metric with ID: "+ metricID + "failed to be comitted to the ledger";
        }

        return mesaage;
    }


}
