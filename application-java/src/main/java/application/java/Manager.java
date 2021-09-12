package application.java;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import io.prometheus.client.exporter.HTTPServer;

public class Manager {

    private ConnectionProfile connectionProfile;
    private LocalStorage localstorage;
    BlockchainAPI api;
    HTTPServer server;


    public Manager(ConnectionProfile connectionProfile) {

        localstorage = LocalStorage.getInstance();
        this.connectionProfile = connectionProfile;
        this.api = new BlockchainAPI();
    }

    public String generateIdentity() throws MalformedURLException, CryptoException, InvalidArgumentException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        IdentityController identityController = new IdentityController(connectionProfile);

        String status = null;
        // enrolls the admin and registers the user
        try {
            status = identityController.EnrollAdmin();
            status += "\n"+ identityController.registerClient();
        } catch (Exception e) {
            System.err.println(e);
            status = "Failed to generate an identity by the name of: " + connectionProfile;
        }

        return status;
    }





    public String createQos (Contract contract, String method, QoS qos)
    {
        String result = null;
        String[] payload = new String[] {qos.getQosID(), qos.getQosName(), qos.getLevel(), qos.getThreshold()};
        System.out.println(payload);

        try {
			// create qyality requirment
			System.out.println("Create Quality Requirement");
			result = api.submitTransaction(contract, method , payload);
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}

         //add this Qos metric to a golable array list to be discoverable by schesuled reporters
		localstorage.getQosStore().put(qos.getQosID(), qos);
        

        System.out.println("Status of creating quality requirement at blockchain side: \n" + result);
        return result;
    }

    
    public ConcurrentHashMap<String, QoS> getQosStore() {
        return localstorage.getQosStore();
	}


    public void printQosStatus(String threadName, String threadID, QoS qos) {
        System.out.println("## Reported incident records about: (" + qos.getQosName() + ") from "+ threadName + " with ID: "
        + threadID +"\n"+
        "----> Current Breaches: " + qos.getBreachCount()+"\n" +
        "----> current Compliant Logs: " + qos.getCompliantCount());

    }




   
    public String reportMetrics (Contract contract, String method, String[] payload)
    {

        String result = null;

        try {
			// create qyality requirment
			System.out.println("reporting metric to the blockchain-side");
			result = api.submitTransaction(contract, method , payload);
		} catch (Exception e) {
			System.err.println("Transaction Failure: " + e);
		}

        return result;

    }

    public void createMetricExporter (int port) throws IOException
    {
        server = new HTTPServer(port);
        
    }



}
