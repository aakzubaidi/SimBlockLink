# MonitoringMiddleware
 enabling transacting between simulators and hyperledger fabric

 # IoTsim-Osmosis Simulator with no integration
 Goal making sure the simulator as expected.
 ## Configuration
 ### Test Cases
 - Test 1: Compliant status 0%
 - Test 2: Compliance status 70% (needs revision)
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

