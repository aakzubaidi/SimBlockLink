/**
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Transaction;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Use this file for functional testing of your smart contract.
 * Fill out the arguments and return values for a function and
 * use the CodeLens links above the transaction blocks to
 * invoke/submit transactions.
 * All transactions defined in your smart contract are used here
 * to generate tests, including those functions that would
 * normally only be used on instantiate and upgrade operations.
 * This basic test file can also be used as the basis for building
 * further functional tests to run as part of a continuous
 * integration pipeline, or for debugging locally deployed smart
 * contracts by invoking/submitting individual transactions.
 *
 * Generating this test file will also modify the build file
 * in the smart contract project directory. This will require
 * the Java classpath/configuration to be synchronized.
 */

public final class FvGatewayGateway002Test {

    Wallet fabricWallet;
    Gateway gateway;
    Gateway.Builder builder;
    Network network;
    Contract contract;
    String homedir = System.getProperty("user.home");
    Path walletPath = Paths.get(homedir, ".fabric-vscode", "v2", "environments", "Fabric", "wallets", "Org1");
    Path connectionProfilePath = Paths.get(homedir, ".fabric-vscode", "v2", "environments", "Fabric", "gateways", "Org1 Gateway.json");
    String identityName = "Org1 Admin";
    boolean isLocalhostURL = JavaSmartContractUtil.hasLocalhostURLs(connectionProfilePath);

    @BeforeEach
    public void before() {
        assertThatCode(() -> {
            JavaSmartContractUtil.setDiscoverAsLocalHost(isLocalhostURL);
            fabricWallet = Wallets.newFileSystemWallet(walletPath);
            builder = Gateway.createBuilder();
            builder.identity(fabricWallet, identityName).networkConfig(connectionProfilePath).discovery(true);
            gateway = builder.connect();
            network = gateway.getNetwork("mychannel");
            contract = network.getContract("Gateway", "Gateway");
        }).doesNotThrowAnyException();
    }

    @AfterEach
    public void after() {
        gateway.close();
    }


    @Nested
    class ReportMetric {
        @Test
        public void submitReportMetricTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String metricID = "EXAMPLE";
            String qosID = "EXAMPLE";
            String compliantCount = "EXAMPLE";
            String breachCount = "EXAMPLE";
            String[] args = new String[]{ metricID, qosID, compliantCount, breachCount };
            Transaction transaction = contract.createTransaction("ReportMetric");
            byte[] response = transaction.submit(args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class PresistQoSt {
        @Test
        public void submitPresistQoStTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String qosID = "EXAMPLE";
            String name = "EXAMPLE";
            String requieredLevel = "EXAMPLE";
            String value = "EXAMPLE";
            String[] args = new String[]{ qosID, name, requieredLevel, value };
            Transaction transaction = contract.createTransaction("presistQoSt");
            byte[] response = transaction.submit(args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }
}