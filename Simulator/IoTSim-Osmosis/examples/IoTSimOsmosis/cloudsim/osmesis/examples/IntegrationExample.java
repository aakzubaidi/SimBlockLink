/*
 * Title:        IoTSim-Osmosis 1.0
 * Description:  IoTSim-Osmosis enables the testing and validation of osmotic computing applications 
 * 			     over heterogeneous edge-cloud SDN-aware environments.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2020, Newcastle University (UK) and Saudi Electronic University (Saudi Arabia) 
 * 
 */

package IoTSimOsmosis.cloudsim.osmesis.examples;

import IoTSimOsmosis.cloudsim.Log;
import IoTSimOsmosis.cloudsim.Vm;
import IoTSimOsmosis.cloudsim.core.CloudSim;
import IoTSimOsmosis.cloudsim.edge.core.edge.ConfiguationEntity;
import IoTSimOsmosis.cloudsim.edge.core.edge.MEL;
import IoTSimOsmosis.cloudsim.edge.utils.LogUtil;
import IoTSimOsmosis.cloudsim.osmesis.examples.uti.GlobalVariable;
import IoTSimOsmosis.cloudsim.osmesis.examples.uti.LogPrinter;
import IoTSimOsmosis.cloudsim.osmesis.examples.uti.PrintResults_Example_3;
import IoTSimOsmosis.cloudsim.sdn.Switch;
import IoTSimOsmosis.osmosis.core.*;
import application.java.Agent;
import application.java.ConnectionProfile;
import application.java.KeyTracker;
import application.java.Manager;
import application.java.QoS;
import application.java.RequieredLevel;
import application.java.Unit;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Contract;

/**
 * 
 * @author Ali Alzubaidi by extending an existing example provided by the IoTsim-Osmosis
 * 
 **/

public class IntegrationExample {
	public static final String configurationFile = "inputFiles/Example3_Configuration_Blockchain.json";
	public static final String osmesisAppFile = "inputFiles/Example3_Worload_Blockchain.csv";
	OsmosisBuilder topologyBuilder;
	OsmesisBroker osmesisBroker;
	List<OsmesisDatacenter> datacenters;
	List<MEL> melList;
	EdgeSDNController edgeSDNController;
	List<Vm> vmList;

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(String[] args) throws Exception {
		ConnectionProfile connectionProfile = new ConnectionProfile();
		connectionProfile.setPemFileLocation(
				"../../../fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
		connectionProfile.setCaClientURL("https://localhost:7054");
		connectionProfile.setAdminIdentity("admin");
		connectionProfile.setAdminSecret("adminpw");
		connectionProfile.setClientIdentity("AliAlzubaidi");
		// how many times to attemp resubmitting failed transactions
		int maxRetry = 5;
		// Do you wish to consider all transactions' resubmission attempts as one failed
		// transaction?
		boolean countfailedAttampt = false;
		
		Manager manager = new Manager(connectionProfile, maxRetry, countfailedAttampt);
		manager.generateIdentity();
		manager.createMetricExporter(8004);
		/**
		 * helper function for getting connected to the gateway
		 * 
		 * @throws IOException
		 */
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "..", "fabric-samples", "test-network", "organizations",
				"peerOrganizations", "org1.example.com", "connection-org1.yaml");

		// Configure the gateway connection used to access the network.
		Gateway.Builder builder = Gateway.createBuilder().identity(wallet, connectionProfile.getClientIdentity())
				.networkConfig(networkConfigPath);

		// Create a gateway connection
		Gateway gateway = builder.connect();
		Network network = gateway.getNetwork("mychannel");
		Contract contract = network.getContract("chaincode");
		// define quality requirement
		// provide (contract, quality metric name, required level, threshold)
		// example (contract, method, latency, LessThan, 2, s)
		manager.getKeyTracker().setMetricKey(1200);
		manager.getKeyTracker().setQosKey(1200);
		
		GlobalVariable globalVariable = GlobalVariable.getInstance();
		globalVariable.setManager(manager);
		QoS qos = new QoS("TransmissionTime", RequieredLevel.LessThan, 3, Unit.s);
		globalVariable.addQoS(qos);
		manager.createQos(contract, "presistQoS", qos);
		
		// schedule workers
        manager.assignQosToWorker(manager, contract, "reportMetric", qos, 0, 1);
        
		 IntegrationExample osmesis = new IntegrationExample();
		 osmesis.start();

	}

	public void start() throws Exception {

		int num_user = 1; // number of users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		// Initialize the CloudSim library
		CloudSim.init(num_user, calendar, trace_flag);
		osmesisBroker = new OsmesisBroker("OsmesisBroker");
		topologyBuilder = new OsmosisBuilder(osmesisBroker);
		ConfiguationEntity config = buildTopologyFromFile(configurationFile);
		if (config != null) {
			topologyBuilder.buildTopology(config);
		}

		OsmosisOrchestrator maestro = new OsmosisOrchestrator();

		OsmesisAppsParser.startParsingExcelAppFile(osmesisAppFile);
		List<SDNController> controllers = new ArrayList<>();
		for (OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()) {
			osmesisBroker.submitVmList(osmesisDC.getVmList(), osmesisDC.getId());
			controllers.add(osmesisDC.getSdnController());
			osmesisDC.getSdnController().setWanOorchestrator(maestro);
		}
		controllers.add(topologyBuilder.getSdWanController());
		maestro.setSdnControllers(controllers);
		osmesisBroker.submitOsmesisApps(OsmesisAppsParser.appList);
		osmesisBroker.setDatacenters(topologyBuilder.getOsmesisDatacentres());

		double startTime = CloudSim.startSimulation();

		LogUtil.simulationFinished();
		PrintResults_Example_3 pr = new PrintResults_Example_3();
		pr.printOsmesisNetwork();

		Log.printLine();

		for (OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()) {
			List<Switch> switchList = osmesisDC.getSdnController().getSwitchList();
			LogPrinter.printEnergyConsumption(osmesisDC.getName(), osmesisDC.getSdnhosts(), switchList, startTime);
			Log.printLine();
		}

		Log.printLine();
		LogPrinter.printEnergyConsumption(topologyBuilder.getSdWanController().getName(), null,
				topologyBuilder.getSdWanController().getSwitchList(), startTime);
		Log.printLine();
		Log.printLine("Simulation Finished!");

	}

	private ConfiguationEntity buildTopologyFromFile(String filePath) throws Exception {
		System.out.println("Creating topology from file " + filePath);
		ConfiguationEntity conf = null;
		try (FileReader jsonFileReader = new FileReader(filePath)) {
			conf = topologyBuilder.parseTopology(jsonFileReader);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: input configuration file not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Topology built:");
		return conf;
	}

	public void setEdgeSDNController(EdgeSDNController edc) {
		this.edgeSDNController = edc;
	}
}
