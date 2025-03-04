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

package IoTSimOsmosis.osmosis.core;


import IoTSimOsmosis.cloudsim.*;
import IoTSimOsmosis.cloudsim.edge.iot.IoTDevice;
import IoTSimOsmosis.cloudsim.edge.iot.network.EdgeNetwork;
import IoTSimOsmosis.cloudsim.edge.iot.network.EdgeNetworkInfo;
import IoTSimOsmosis.cloudsim.edge.utils.LogUtil;
import IoTSimOsmosis.cloudsim.sdn.Switch;
import IoTSimOsmosis.cloudsim.sdn.example.policies.VmAllocationPolicyCombinedMostFullFirst;
import IoTSimOsmosis.cloudsim.sdn.example.policies.VmSchedulerTimeSharedEnergy;
import IoTSimOsmosis.cloudsim.sdn.power.PowerUtilizationMaxHostInterface;
import com.google.gson.Gson;
import IoTSimOsmosis.cloudsim.edge.core.edge.ConfiguationEntity;
import IoTSimOsmosis.cloudsim.edge.core.edge.EdgeDataCenter;
import IoTSimOsmosis.cloudsim.edge.core.edge.EdgeDevice;
import IoTSimOsmosis.cloudsim.edge.core.edge.MEL;
import IoTSimOsmosis.cloudsim.edge.core.edge.Mobility;
import IoTSimOsmosis.cloudsim.edge.iot.protocol.AMQPProtocol;
import IoTSimOsmosis.cloudsim.edge.iot.protocol.CoAPProtocol;
import IoTSimOsmosis.cloudsim.edge.iot.protocol.IoTProtocol;
import IoTSimOsmosis.cloudsim.edge.iot.protocol.MQTTProtocol;
import IoTSimOsmosis.cloudsim.edge.iot.protocol.XMPPProtocol;
import IoTSimOsmosis.cloudsim.provisioners.BwProvisioner;
import IoTSimOsmosis.cloudsim.provisioners.BwProvisionerSimple;
import IoTSimOsmosis.cloudsim.provisioners.PeProvisionerSimple;
import IoTSimOsmosis.cloudsim.provisioners.RamProvisioner;
import IoTSimOsmosis.cloudsim.provisioners.RamProvisionerSimple;
import IoTSimOsmosis.osmosis.core.polocies.SDNTrafficPolicyFairShare;
import IoTSimOsmosis.osmosis.core.polocies.SDNTrafficSchedulingPolicy;
import IoTSimOsmosis.osmosis.core.polocies.SDNRoutingLoadBalancing;
import IoTSimOsmosis.osmosis.core.polocies.SDNRoutingPolicy;
import IoTSimOsmosis.osmosis.core.polocies.VmMELAllocationPolicyCombinedLeastFullFirst;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class OsmosisBuilder {    
	private OsmesisBroker  broker;
	List<CloudDatacenter> cloudDatacentres;
	public static  List<EdgeDataCenter> edgeDatacentres;
	public static int flowId = 1;
	public static int edgeLetId = 1;
	private SDNController sdWanController;
	
	public static int hostId = 1;
	private static int vmId = 1;
	
	public SDNController getSdWanController() {
		return sdWanController;
	}

	private List<OsmesisDatacenter> osmesisDatacentres; 


	public List<EdgeDataCenter> getEdgeDatacentres() {
		return edgeDatacentres;
	}
	  public List<CloudDatacenter> getCloudDatacentres() {
		return cloudDatacentres;
	}
	  
    public OsmosisBuilder(OsmesisBroker osmesisBroker) {
    	this.broker = osmesisBroker;
    	this.osmesisDatacentres = new ArrayList<>();
	}

	  public List<OsmesisDatacenter> getOsmesisDatacentres() {
		return osmesisDatacentres;
	}
    
	public ConfiguationEntity parseTopology(FileReader jsonFileReader) {
        Gson gson = new Gson();
        ConfiguationEntity conf = gson.fromJson(jsonFileReader, ConfiguationEntity.class);
        return conf;
    }

    public void buildTopology(ConfiguationEntity topologyEntity) throws Exception {
        List<ConfiguationEntity.CloudDataCenterEntity> datacentreEntities = topologyEntity.getCloudDatacenter();
        this.cloudDatacentres = buildCloudDatacentres(datacentreEntities);        
        List<ConfiguationEntity.EdgeDataCenterEntity> edgeDatacenerEntites = topologyEntity.getEdgeDatacenter();
        this.edgeDatacentres = buildEdgeDatacentres(edgeDatacenerEntites);        
        initLog(topologyEntity);        
        osmesisDatacentres.addAll(this.cloudDatacentres);
        osmesisDatacentres.addAll(this.edgeDatacentres);        
        List<Switch> datacenterGateways = new ArrayList<>();
		
        for(OsmesisDatacenter osmesisDC : this.getOsmesisDatacentres()){
			datacenterGateways.add(osmesisDC.getSdnController().getGateway());			
		
        }		
        List<ConfiguationEntity.WanEntity> wanEntities = topologyEntity.getSdwan();
        this.sdWanController = buildWan(wanEntities, datacenterGateways);
        setWanControllerToDatacenters(sdWanController, osmesisDatacentres);
        sdWanController.addAllDatacenters(osmesisDatacentres);
    }

    /*
     * 
     * Create Cloud DataCenters 
     * 
     */
    
    protected List<CloudDatacenter> buildCloudDatacentres(List<ConfiguationEntity.CloudDataCenterEntity> datacentreEntities) throws Exception{
        List<CloudDatacenter> datacentres = new ArrayList<CloudDatacenter>();

        for(ConfiguationEntity.CloudDataCenterEntity datacentreEntity: datacentreEntities){
            // Assumption: every datacentre only has one controller
            SDNController sdnController = createCloudSDNController(datacentreEntity.getControllers().get(0));                       
            VmAllocationPolicyFactory vmAllocationPolicyFactory = null;
            if(datacentreEntity.getVmAllocationPolicy().equals("VmAllocationPolicyCombinedFullFirst")){
            	vmAllocationPolicyFactory = hostList -> new VmAllocationPolicyCombinedMostFullFirst();
            }
            if(datacentreEntity.getVmAllocationPolicy().equals("VmAllocationPolicyCombinedLeastFullFirst")){
            	vmAllocationPolicyFactory = hostList -> new VmMELAllocationPolicyCombinedLeastFullFirst();
            }

            CloudDatacenter datacentre = createCloudDatacenter(
                    datacentreEntity.getName(),
                    sdnController,                    
                    vmAllocationPolicyFactory
            );
            datacentre.initCloudTopology(datacentreEntity.getHosts(),datacentreEntity.getSwitches(),
            		datacentreEntity.getLinks());
            datacentre.feedSDNWithTopology(sdnController);
            datacentre.setGateway(datacentre.getSdnController().getGateway());
            datacentre.setDcType(datacentreEntity.getType());
            List<Vm> vmList = createVMs(datacentreEntity.getVMs());
            
			for(Vm mel : vmList){
				datacentre.mapVmNameToID(mel.getId(), mel.getVmName());
			}
			
			this.broker.mapVmNameToId(datacentre.getVmNameToIdList());
            
            sdnController.setDatacenter(datacentre);        
            sdnController.addVmsToSDNhosts(vmList);
            datacentre.getVmAllocationPolicy().setUpVmTopology(datacentre.getHosts());
            datacentre.setVmList(vmList);
            datacentres.add(datacentre);
        }

        return datacentres;
    }
    
    protected CloudDatacenter createCloudDatacenter(String name, SDNController sdnController,                                                            
                                                             VmAllocationPolicyFactory vmAllocationFactory) {
  
        List<Host> hostList = sdnController.getHostList();
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        // Create Datacenter with previously set parameters
        CloudDatacenter datacenter = null;
        try {
            VmAllocationPolicy vmPolicy = vmAllocationFactory.create(hostList);
            System.out.println(vmPolicy.getPolicyName());
            if(vmPolicy instanceof VmAllocationPolicyCombinedMostFullFirst){
                vmPolicy.setPolicyName("CombinedMostFullFirst");
            } else if(vmPolicy instanceof VmMELAllocationPolicyCombinedLeastFullFirst){
                vmPolicy.setPolicyName("CombinedLeastFullFirst");
            }
            // Why to use maxHostHandler!
            PowerUtilizationMaxHostInterface maxHostHandler = (PowerUtilizationMaxHostInterface)vmPolicy;
            datacenter = new CloudDatacenter(name, characteristics, vmPolicy, storageList, 0, sdnController);
            
            sdnController.setDatacenter(datacenter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    protected SDNController createCloudSDNController(ConfiguationEntity.ControllerEntity controllerEntity){

        SDNTrafficSchedulingPolicy sdnMapReducePolicy = null;
        SDNRoutingPolicy sdnRoutingPolicy = null;
        String sdnName = controllerEntity.getName();
        switch (controllerEntity.getRoutingPolicy()){
            case "ShortestPathBw":
                sdnRoutingPolicy = new SDNRoutingLoadBalancing();
                break;
        }

        switch (controllerEntity.getTrafficPolicy()){
            case "FairShare":
                sdnMapReducePolicy = new SDNTrafficPolicyFairShare();
                break;
        }

        SDNController sdnController = new CloudSDNController(controllerEntity.getName(),sdnMapReducePolicy, sdnRoutingPolicy);
        sdnController.setName(sdnName);
        return sdnController;
    }

    private List<Vm> createVMs(List<ConfiguationEntity.VMEntity> vmEntites) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> vmList = new ArrayList<Vm>();
        
        for(ConfiguationEntity.VMEntity vmEntity : vmEntites){
            //VM Parameters
            int pesNumber = vmEntity.getPes(); //number of cpus
            int mips = (int) vmEntity.getMips();
            int ram = vmEntity.getRam(); //vm memory (MB)
            long size = (long) vmEntity.getStorage(); //image size (MB)
            long bw = vmEntity.getBw();            

            String vmm = "Xen"; //VMM name

            //create VMs
            Vm vm = new Vm(vmId, this.broker.getId(), mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());            
            vm.setVmName(vmEntity.getName());
            vmList.add(vm);   
            vmId++;
            }
        return vmList;
    }
    
    
  /*
   * 
   * Build SD-WAN network  
   * 
   */    

    private SDNController buildWan(List<ConfiguationEntity.WanEntity> wanEntity, List<Switch> datacenterGateways) {
        for(ConfiguationEntity.WanEntity wan : wanEntity){
            // Assumption: every datacentre only has one controller            
        	sdWanController = createWanController(wan.getControllers());
        	sdWanController.initSdWANTopology(wan.getSwitches(), wan.getLinks(),datacenterGateways);
        }               
        return sdWanController;
    }
    
    private void setWanControllerToDatacenters(SDNController wanController, List<OsmesisDatacenter> datacentres) {
        for (OsmesisDatacenter datacenter: datacentres) {
            SDNController controller = datacenter.getSdnController();
            controller.setWanController(wanController);
        }
    }
 
    protected SDNController createWanController(ConfiguationEntity.ControllerEntity controllerEntity){
        SDNTrafficSchedulingPolicy sdnMapReducePolicy = null;
        SDNRoutingPolicy sdnRoutingPolicy = null;
       
        String sdnName = controllerEntity.getName();

        switch (controllerEntity.getRoutingPolicy()){
            case "ShortestPathBw":
                sdnRoutingPolicy = new SDNRoutingLoadBalancing();
                break;
        }

        switch (controllerEntity.getTrafficPolicy()){
            case "FairShare":
                sdnMapReducePolicy = new SDNTrafficPolicyFairShare();
                break;
        }

        SDNController sdnController = new SDWANController(controllerEntity.getName(),sdnMapReducePolicy, sdnRoutingPolicy);
        sdnController.setName(sdnName);
        return sdnController;
    }
    
  /*
   * 
   * Build Edge Datacenters 
   * 
   */

    private List<EdgeDataCenter> buildEdgeDatacentres(List<ConfiguationEntity.EdgeDataCenterEntity> edgeDatacenerEntites) {
        List<EdgeDataCenter> edgeDC = new ArrayList<EdgeDataCenter>();

    	for(ConfiguationEntity.EdgeDataCenterEntity edgeDCEntity : edgeDatacenerEntites){
			List<ConfiguationEntity.EdgeDeviceEntity> hostListEntities = edgeDCEntity.getHosts();
			List<EdgeDevice> hostList = new ArrayList<EdgeDevice>();
			try {
				for (ConfiguationEntity.EdgeDeviceEntity hostEntity : hostListEntities) {
					ConfiguationEntity.VmAllcationPolicyEntity vmSchedulerEntity = edgeDCEntity.getVmAllocationPolicy();
					String vmSchedulerClassName = vmSchedulerEntity.getClassName();												
					LinkedList<Pe> peList = new LinkedList<Pe>();
					int peId=0;
					for(int i= 0; i < hostEntity.getPes(); i++) {
						peList.add(new Pe(peId++,new PeProvisionerSimple(hostEntity.getMips())));
					}
					
					RamProvisioner ramProvisioner = new RamProvisionerSimple(hostEntity.getRamSize());
					BwProvisioner bwProvisioner = new BwProvisionerSimple(hostEntity.getBwSize());
					VmScheduler vmScheduler = new VmSchedulerTimeSharedEnergy(peList);
	
					EdgeDevice edgeDevice = new EdgeDevice(hostId, hostEntity.getName(), ramProvisioner, bwProvisioner,
							hostEntity.getStorage(), peList, vmScheduler);
							
					hostList.add(edgeDevice);
					hostId++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			ConfiguationEntity.EdgeDatacenterCharacteristicsEntity characteristicsEntity = edgeDCEntity.getCharacteristics();
			String architecture = characteristicsEntity.getArchitecture();
			String os = characteristicsEntity.getOs();
			String vmm = characteristicsEntity.getVmm();
			double timeZone = characteristicsEntity.getTimeZone();
			double costPerMem = characteristicsEntity.getCostPerMem();
			double cost = characteristicsEntity.getCost();
	
			double costPerStorage = characteristicsEntity.getCostPerStorage();
			double costPerBw = characteristicsEntity.getCostPerBw();
			LinkedList<Storage> storageList = new LinkedList<Storage>();
			
			
					
			DatacenterCharacteristics characteristics = new DatacenterCharacteristics(architecture, os, vmm,
					hostList, timeZone, cost, costPerMem, costPerStorage, costPerBw);	
	
			ConfiguationEntity.VmAllcationPolicyEntity vmAllcationPolicyEntity = edgeDCEntity.getVmAllocationPolicy();
			String className = vmAllcationPolicyEntity.getClassName();
	
			// 6. Finally, we need to create a PowerDatacenter object.
			EdgeDataCenter datacenter = null;
			VmAllocationPolicy vmAllocationPolicy = null; 
			try {
				switch(className){
					case "VmAllocationPolicyCombinedLeastFullFirst":
					vmAllocationPolicy = new VmMELAllocationPolicyCombinedLeastFullFirst();
				break;
			}
				datacenter = new EdgeDataCenter(edgeDCEntity.getName(), characteristics, vmAllocationPolicy, storageList,
						edgeDCEntity.getSchedulingInterval());// , switchesList);
				datacenter.setDcType(edgeDCEntity.getType());
	
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			for(ConfiguationEntity.ControllerEntity controller : edgeDCEntity.getControllers()){
				SDNController edgeSDNController = creatEdgeSDNController(controller);
				datacenter.setSdnController(edgeSDNController);
				edgeSDNController.setDatacenter(datacenter);
			}
			System.out.println("Edge SDN cotroller has been created");
			datacenter.initEdgeTopology(hostList, edgeDCEntity.getSwitches(),edgeDCEntity.getLinks());			
			datacenter.feedSDNWithTopology(datacenter.getSdnController());
			
			datacenter.setGateway(datacenter.getSdnController().getGateway());
			
			List<MEL> MELList = createMEL(edgeDCEntity.getMELEntities(), datacenter.getId(), this.broker);
			datacenter.setVmList(MELList);
			
			for(MEL mel : MELList){
				datacenter.mapVmNameToID(mel.getId(), mel.getVmName());
			}
			
			this.broker.mapVmNameToId(datacenter.getVmNameToIdList());
			datacenter.getVmAllocationPolicy().setUpVmTopology(hostList);
			datacenter.getSdnController().addVmsToSDNhosts(MELList);
			
			List<IoTDevice> devices = createIoTDevice(edgeDCEntity.getIoTDevices());
			this.broker.setIoTDevices(devices);
			edgeDC.add(datacenter);
    	}		
		return edgeDC;
	}
    
    protected SDNController creatEdgeSDNController(ConfiguationEntity.ControllerEntity controllerEntity){
        SDNTrafficSchedulingPolicy sdnMapReducePolicy = null;
        SDNRoutingPolicy sdnRoutingPolicy = null;       
        String sdnName = controllerEntity.getName();

        switch (controllerEntity.getRoutingPolicy()){
            case "ShortestPathBw":
                sdnRoutingPolicy = new SDNRoutingLoadBalancing();
                break;
        }

        switch (controllerEntity.getTrafficPolicy()){
            case "FairShare":
                sdnMapReducePolicy = new SDNTrafficPolicyFairShare();
                break;
        }

        SDNController sdnController = new EdgeSDNController(controllerEntity.getName(),sdnMapReducePolicy, sdnRoutingPolicy);
        sdnController.setName(sdnName);
        return sdnController;
    }
    
	private List<MEL> createMEL(List<ConfiguationEntity.MELEntities> melEntities, int edgeDatacenterId, OsmesisBroker broker) {
		List<MEL> vms = new ArrayList<>();
		for (ConfiguationEntity.MELEntities melEntity : melEntities) {

			String cloudletSchedulerClassName = melEntity.getCloudletSchedulerClassName();
			CloudletScheduler cloudletScheduler;
			try {				

				cloudletScheduler = (CloudletScheduler) Class.forName(cloudletSchedulerClassName).newInstance();
				float datasizeShrinkFactor = melEntity.getDatasizeShrinkFactor();				
				MEL microELement = new MEL(edgeDatacenterId, vmId, broker.getId(), melEntity.getMips(),
						melEntity.getPesNumber(), melEntity.getRam(), melEntity.getBw(),
						melEntity.getVmm(), cloudletScheduler, datasizeShrinkFactor);				
				microELement.setVmName(melEntity.getName());
				vms.add(microELement);
				vmId++;
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
		return vms;
	}
		
	private List<IoTDevice> createIoTDevice(List<ConfiguationEntity.IotDeviceEntity>  iotDeviceEntity) {
		List<IoTDevice> devices = new ArrayList<>();
		for(ConfiguationEntity.IotDeviceEntity iotDevice : iotDeviceEntity){
			String ioTClassName = iotDevice.getIoTClassName();
			ConfiguationEntity.NetworkModelEntity networkModelEntity = iotDevice.getNetworkModelEntity();
			// xmpp, mqtt, coap, amqp
			EdgeNetworkInfo networkModel = this.SetEdgeNetworkModel(networkModelEntity);
			try {
				Class<?> clazz = Class.forName(ioTClassName);
				if (!IoTDevice.class.isAssignableFrom(clazz)) {
					System.out.println("this class is not correct type of ioT Device");
					return null;
				}
				Constructor<?> constructor = clazz.getConstructor(EdgeNetworkInfo.class, String.class, double.class);
	
					IoTDevice newInstance = (IoTDevice) constructor.newInstance(networkModel, iotDevice.getName(), iotDevice.getBw());		
					newInstance.getBattery().setMaxCapacity(iotDevice.getMax_battery_capacity());
					newInstance.getBattery().setCurrentCapacity(iotDevice.getMax_battery_capacity());
					newInstance.getBattery().setBatterySensingRate(iotDevice.getBattery_sensing_rate());
					newInstance.getBattery().setBatterySendingRate(iotDevice.getBattery_sending_rate());;
					
					Mobility location = new Mobility(iotDevice.getMobilityEntity().getLocation());
					location.movable = iotDevice.getMobilityEntity().isMovable();
					if (iotDevice.getMobilityEntity().isMovable()) {
						location.range = new Mobility.MovingRange(iotDevice.getMobilityEntity().getRange().beginX,
								iotDevice.getMobilityEntity().getRange().endX);
						location.signalRange = iotDevice.getMobilityEntity().getSignalRange();
						location.velocity = iotDevice.getMobilityEntity().getVelocity();
					}
					newInstance.setMobility(location);
	
					devices.add(newInstance);						
	
			} catch (ClassCastException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return devices;
	}

	private EdgeNetworkInfo SetEdgeNetworkModel(ConfiguationEntity.NetworkModelEntity networkModelEntity) {
		String communicationProtocolName = networkModelEntity.getCommunicationProtocol();
		communicationProtocolName = communicationProtocolName.toLowerCase();
		IoTProtocol communicationProtocol = null;
		switch (communicationProtocolName) {
		case "xmpp":
			communicationProtocol = new XMPPProtocol();
			break;
		case "mqtt":
			communicationProtocol = new MQTTProtocol();
			break;
		case "coap":
			communicationProtocol = new CoAPProtocol();
			break;
		case "amqp":
			communicationProtocol = new AMQPProtocol();
			break;
		default:
			System.out.println("have not supported protocol " + communicationProtocol + " yet!");
			return null;
		}
		String networkTypeName = networkModelEntity.getNetworkType();
		networkTypeName = networkTypeName.toLowerCase();
	
		EdgeNetwork edgeNetwork = new EdgeNetwork(networkTypeName);
		EdgeNetworkInfo networkModel = new EdgeNetworkInfo(edgeNetwork, communicationProtocol);		
		return networkModel;
	}
	
	private void initLog(ConfiguationEntity conf) {
		ConfiguationEntity.LogEntity logEntity = conf.getLogEntity();
		boolean saveLogToFile = logEntity.isSaveLogToFile();
		if (saveLogToFile) {
			String logFilePath = logEntity.getLogFilePath();
			String logLevel = logEntity.getLogLevel();
			boolean append = logEntity.isAppend();
			LogUtil.initLog(LogUtil.Level.valueOf(logLevel.toUpperCase()), logFilePath, saveLogToFile, append);
		}
	}	
}
