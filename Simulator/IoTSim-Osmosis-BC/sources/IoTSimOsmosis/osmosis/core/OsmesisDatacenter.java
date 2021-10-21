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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import IoTSimOsmosis.cloudsim.core.SimEvent;
import IoTSimOsmosis.cloudsim.edge.core.edge.ConfiguationEntity;
import IoTSimOsmosis.cloudsim.edge.core.edge.EdgeDevice;
import IoTSimOsmosis.cloudsim.sdn.SDNHost;
import IoTSimOsmosis.cloudsim.sdn.Switch;
import IoTSimOsmosis.cloudsim.sdn.example.policies.VmSchedulerTimeSharedEnergy;
import IoTSimOsmosis.cloudsim.Datacenter;
import IoTSimOsmosis.cloudsim.DatacenterCharacteristics;
import IoTSimOsmosis.cloudsim.Host;
import IoTSimOsmosis.cloudsim.Pe;
import IoTSimOsmosis.cloudsim.Storage;
import IoTSimOsmosis.cloudsim.VmAllocationPolicy;
import IoTSimOsmosis.cloudsim.VmScheduler;
import IoTSimOsmosis.cloudsim.provisioners.BwProvisioner;
import IoTSimOsmosis.cloudsim.provisioners.BwProvisionerSimple;
import IoTSimOsmosis.cloudsim.provisioners.PeProvisionerSimple;
import IoTSimOsmosis.cloudsim.provisioners.RamProvisioner;
import IoTSimOsmosis.cloudsim.provisioners.RamProvisionerSimple;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public abstract class OsmesisDatacenter extends Datacenter {

	private Switch gateway;
	private String dcType;
	private Map<String, Integer> vmNameToId = new HashMap<>();
	
	protected Topology topology;
	protected SDNController sdnController;
	protected List<Host> hosts = new ArrayList<>();
	protected List<SDNHost> sdnhosts;
	protected List<Switch> switches;
	public static int cloudletsNumers = 0;
	int lastProcessTime; 	

	public abstract void initCloudTopology(List<ConfiguationEntity.HostEntity> hostEntites, List<ConfiguationEntity.SwitchEntity> switchEntites, List<ConfiguationEntity.LinkEntity> linkEntites);

	public abstract void initEdgeTopology(List<EdgeDevice> devices, List<ConfiguationEntity.SwitchEntity> switchEntites, List<ConfiguationEntity.LinkEntity> linkEntites);
		
	public OsmesisDatacenter(String name, DatacenterCharacteristics characteristics,
                             VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
			throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
	}
	
	public List<Host> getHosts() {
		return hosts;
	}
	
	public String getDcType() {
		return dcType;
	}

	public void setDcType(String dcType) {
		this.dcType = dcType;
	}
	
	public SDNController getSdnController() {
		return sdnController;
	}
	
	public void setSdnController(SDNController sdnController) {
		this.sdnController = sdnController;
	}

	public void setHostsList(List<Host> hosts){
		this.hosts = hosts;
	}
	
	public void feedSDNWithTopology(SDNController controller){
		controller.setTopology(topology, hosts, sdnhosts, switches);
	}
	
	@Override
	public void processOtherEvent(SimEvent ev){
		switch(ev.getTag()){	
			default:
				System.out.println("Unknown event recevied by SDNDatacenter. Tag:"+ev.getTag());
				break;
		}
	}	
		
	protected Host createHost(int hostId, int ram, long bw, long storage, long pes, double mips) {
		LinkedList<Pe> peList = new LinkedList<Pe>();
		int peId=0;
		for(int i=0;i<pes;i++) peList.add(new Pe(peId++,new PeProvisionerSimple(mips)));
		
		RamProvisioner ramPro = new RamProvisionerSimple(ram);
		BwProvisioner bwPro = new BwProvisionerSimple(bw);
		VmScheduler vmScheduler = new VmSchedulerTimeSharedEnergy(peList);
		Host newHost = new Host(hostId, ramPro, bwPro, storage, peList, vmScheduler);
		
		return newHost;		
	}

	public void setGateway(Switch gateway) {		
		this.gateway = gateway;
	}	
	
	public Switch getGateway() {
		return gateway;
	}

	public void mapVmNameToID(int id, String vmName) {
		vmNameToId.put(vmName, id);
	}
	
	public int getVmIdByName(String melName){
		return this.vmNameToId.get(melName);
	}
	
	public Map<String, Integer> getVmNameToIdList(){
		return this.vmNameToId;
	}
	
	public List<SDNHost> getSdnhosts() {
		return sdnhosts;
	}

	public void setSdnhosts(List<SDNHost> sdnhosts) {
		this.sdnhosts = sdnhosts;
	}
}
