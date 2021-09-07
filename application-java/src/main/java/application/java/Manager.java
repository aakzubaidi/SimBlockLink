package application.java;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

public class Manager {

    private ConnectionProfile connectionProfile;
    private LocalStorage localstorage;


    public Manager(ConnectionProfile connectionProfile) {

        localstorage = LocalStorage.getInstance();
        this.connectionProfile = connectionProfile;
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




    public String addQosToConcurrentStorage (QoS qoS)
    {
        

        //add this Qos metric to a golable array list to be discoverable by schesuled reporters
		localstorage.getQosStore().put(qoS.getQosID(), qoS);

        return localstorage.getQosStore().get(qoS.getQosID()).toString();
    }

}
