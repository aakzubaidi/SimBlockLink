package application.java;

/**
 * @apiNote a singleton connection profile
 *          and blockchain helpers.
 * @author Ali Alzubaidi
 * @date 2021
 */


// Java program implementing Singleton class
// with getInstance() method
public class ConnectionProfile {
   
    //certificate file location
    private String pemFileLocation;
    //link to CA-client
    private String caClientURL;
    //Admin Identity
    private String adminIdentity;
    //Admin Secret
    private String adminSecret;
    // client Identity
    private String clientIdentity;

    // private constructor restricted to this class itself
    public ConnectionProfile() {

    }

 

    public String getPemFileLocation() {
        return pemFileLocation;
    }

    public void setPemFileLocation(String pemFileLocation) {
        this.pemFileLocation = pemFileLocation;
    }

    public String getCaClientURL() {
        return caClientURL;
    }

    public void setCaClientURL(String caClientURL) {
        this.caClientURL = caClientURL;
    }

    public String getAdminIdentity() {
        return adminIdentity;
    }

    public void setAdminIdentity(String adminIdentity) {
        this.adminIdentity = adminIdentity;
    }

    public String getAdminSecret() {
        return adminSecret;
    }

    public void setAdminSecret(String adminSecret) {
        this.adminSecret = adminSecret;
    }

    public String getClientIdentity() {
        return clientIdentity;
    }

    public void setClientIdentity(String clientIdentity) {
        this.clientIdentity = clientIdentity;
    }

}