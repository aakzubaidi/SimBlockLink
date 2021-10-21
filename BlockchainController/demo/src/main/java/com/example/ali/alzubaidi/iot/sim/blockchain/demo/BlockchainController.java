package com.example.ali.alzubaidi.iot.sim.blockchain.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @author Ali Alzubaidi
 * @since 14 Oct 2021
 */
public class BlockchainController {



    public static File testNetworkDirecotry = new File("/Users/aliaalzubaidi/fabric-samples/test-network");
   
    public static void main(String[] args) throws IOException, InterruptedException {

        Process buildNetwork = Runtime.getRuntime().exec("./network.sh up createChannel -ca -c mychannel -s couchdb", null, testNetworkDirecotry);
        printResults(buildNetwork);
        Process deploySmartContract = Runtime.getRuntime().exec("./network.sh deployCC -ccn chaincode -ccp ./MonitoringMiddleware -ccv 0.0.1 -ccl java", null, testNetworkDirecotry);
        printResults(deploySmartContract);
        Process destoryNetwork = Runtime.getRuntime().exec("./network.sh down", null, testNetworkDirecotry);
        printResults(destoryNetwork);
    }

    public static void printResults(Process process) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor(3, TimeUnit.SECONDS);
    }

}
