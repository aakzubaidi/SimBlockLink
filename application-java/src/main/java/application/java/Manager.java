package application.java;

public class Manager {

    private LocalStorage localstorage;

    public Manager() {

        localstorage = LocalStorage.getInstance();
    }

    public String generateIdentity(String IdentityName) {

        String status = null;
        // choose a an identity name for the simulator side.
        localstorage.setmanagerIdentity(IdentityName);
        // enrolls the admin and registers the user
        try {
            EnrollAdmin.main(null);
            RegisterUser.main(null);
            status = "sucesfully generated an identity by the name of: " + IdentityName;
        } catch (Exception e) {
            System.err.println(e);
            status = "Failed to generate an identity by the name of: " + IdentityName;
        }

        return status;
    }



    public String getIdentity ()
    {
        return localstorage.getmanagerIdentity();
    }


    public String addQosToConcurrentStorage (QoS qoS)
    {
        

        //add this Qos metric to a golable array list to be discoverable by schesuled reporters
		localstorage.getIncidentsLocalStore().put(qoS.getQosID(), qoS);

        return localstorage.getIncidentsLocalStore().get(qoS.getQosID()).toString();
    }

}
