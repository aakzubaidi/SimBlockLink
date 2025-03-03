# SimBlockLink
 enabling transacting between simulators and hyperledger fabric

 # IoTsim-Osmosis Simulator with no integration
 Goal making sure the simulator as expected.
 ## Configuration
 ### Test Cases
 - Test 1: Compliant status 0%
 - Test 2: Compliance status 66.6%
 - Test 3: Compliance status 100%

 # Blockchain deployment
 ```sh
 cd fabric-samples
 cd test-network
 ```
Bring up the network:

```sh
 ./network.sh up createChannel -ca -c mychannel -s couchdb
```

Deploy Gateway smart contract

```sh
./network.sh deployCC -ccn chaincode -ccp ./MonitoringMiddleware -ccv 0.0.1 -ccl java
```


# Integrating the middleware with the simulator
## import jar file 'application-java.jar'
## within the main method, include the following:
###
```java
ConnectionProfile connectionProfile = new ConnectionProfile();
connectionProfile.setPemFileLocation("../../fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
connectionProfile.setCaClientURL("https://localhost:7054");
connectionProfile.setAdminIdentity("admin");
connectionProfile.setAdminSecret("adminpw");
connectionProfile.setClientIdentity("AliAlzubaidi2");
```

