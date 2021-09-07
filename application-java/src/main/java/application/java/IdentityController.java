package application.java;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.Set;

public class IdentityController {

    private ConnectionProfile connectionProfile;
    private Properties props;
    private HFCAClient caClient;
    private CryptoSuite cryptoSuite;
    public IdentityController(ConnectionProfile connectionProfile) throws MalformedURLException, CryptoException, InvalidArgumentException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        this.connectionProfile = connectionProfile;
        // Create a CA client for interacting with the CA.
        this.props = new Properties();
		props.put("pemFile", connectionProfile.getPemFileLocation());
		props.put("allowAllHostNames", "true");
		this.caClient = HFCAClient.createNewInstance(connectionProfile.getCaClientURL(), props);
		cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);
    }

    public String EnrollAdmin () throws IOException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, CertificateException
    {
        String result = null;
        // Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

		// Check to see if we've already enrolled the admin user.
		if (wallet.get("admin") != null) {
            result = "An identity for the admin user ("+ connectionProfile.getAdminIdentity() +") already exists in the wallet";
			System.out.println(result);
			return result;
		}

		// Enroll the admin user, and import the new identity into the wallet.
		final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
		enrollmentRequestTLS.addHost("localhost");
		enrollmentRequestTLS.setProfile("tls");
		Enrollment enrollment = caClient.enroll(connectionProfile.getAdminIdentity(), connectionProfile.getAdminSecret(), enrollmentRequestTLS);
		Identity user = Identities.newX509Identity("Org1MSP", enrollment);
		wallet.put(connectionProfile.getAdminIdentity(), user);
		System.out.println("Successfully enrolled user ("+ connectionProfile.getAdminIdentity() +") and imported it into the wallet");



        return result;
    }

    public String registerClient () throws Exception
    {
        String result = null;
        // Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));


        // Check to see if we've already enrolled the user.
		if (wallet.get(connectionProfile.getClientIdentity())!= null) {
            result = "An identity for the user ("+ connectionProfile.getClientIdentity()
            + ") already exists in the wallet";;
			return result;
		}

		X509Identity adminIdentity = (X509Identity)wallet.get(connectionProfile.getAdminIdentity());
		if (adminIdentity == null) {
            result = connectionProfile.getAdminIdentity()+ " needs to be enrolled and added to the wallet first";
			System.out.println();
			return result;
		}
		
		User admin = new User() {

			@Override
			public String getName() {
				return "admin";
			}

			@Override
			public Set<String> getRoles() {
				return null;
			}

			@Override
			public String getAccount() {
				return null;
			}

			@Override
			public String getAffiliation() {
				return "org1.department1";
			}

			@Override
			public Enrollment getEnrollment() {
				return new Enrollment() {

					@Override
					public PrivateKey getKey() {
						return adminIdentity.getPrivateKey();
					}

					@Override
					public String getCert() {
						return Identities.toPemString(adminIdentity.getCertificate());
					}
				};
			}

			@Override
			public String getMspId() {
				return "Org1MSP";
			}

		};

		// Register the user, enroll the user, and import the new identity into the wallet.
		RegistrationRequest registrationRequest = new RegistrationRequest(connectionProfile.getClientIdentity());
		//registrationRequest.setAffiliation("org1.department1");
		registrationRequest.setEnrollmentID(connectionProfile.getClientIdentity());
		String enrollmentSecret = caClient.register(registrationRequest, admin);
		Enrollment enrollment = caClient.enroll(connectionProfile.getClientIdentity(), enrollmentSecret);
		Identity user = Identities.newX509Identity("Org1MSP", enrollment);
		wallet.put(connectionProfile.getClientIdentity(), user);
		System.out.println("Successfully enrolled user ("+ connectionProfile.getClientIdentity()
				+ ") and imported it into the wallet");


        return result;
    }
    
    
}
