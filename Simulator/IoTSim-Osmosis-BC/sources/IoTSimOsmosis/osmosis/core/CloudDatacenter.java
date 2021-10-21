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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import IoTSimOsmosis.cloudsim.core.CloudSim;
import IoTSimOsmosis.cloudsim.core.SimEvent;
import IoTSimOsmosis.cloudsim.edge.core.edge.ConfiguationEntity;
import IoTSimOsmosis.cloudsim.edge.core.edge.EdgeDevice;
import IoTSimOsmosis.cloudsim.sdn.SDNHost;
import IoTSimOsmosis.cloudsim.sdn.Switch;
import IoTSimOsmosis.cloudsim.DatacenterCharacteristics;
import IoTSimOsmosis.cloudsim.Host;
import IoTSimOsmosis.cloudsim.Storage;
import IoTSimOsmosis.cloudsim.Vm;
import IoTSimOsmosis.cloudsim.VmAllocationPolicy;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class CloudDatacenter extends OsmesisDatacenter {

		public static int cloudletsNumers = 0;
		int lastProcessTime;	

		public CloudDatacenter(String name,
								DatacenterCharacteristics characteristics,
								VmAllocationPolicy vmAllocationPolicy,
								List<Storage> storageList,
								double schedulingInterval,
								SDNController sdnController) throws Exception {
			super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);		
			this.sdnController = sdnController;		
		}

		public void addVm(Vm vm){
			getVmList().add(vm);
			if (vm.isBeingInstantiated()) vm.setBeingInstantiated(false);
			vm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(vm).getVmScheduler().getAllocatedMipsForVm(vm));
		}
		
		@Override
		public void processOtherEvent(SimEvent ev){
			int tag = ev.getTag();
			switch(tag){
				case OsmosisTags.BUILD_ROUTE:
					sendMelDataToClouds(ev);
					break;
				default: System.out.println("Unknown event recevied by SDNDatacenter. Tag:"+ev.getTag());
			}
		}
		
		public Map<String, Integer> getVmNameIdTable() {
			return this.sdnController.getVmNameIdTable();
		}
		public Map<String, Integer> getFlowNameIdTable() {
			return this.sdnController.getFlowNameIdTable();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "{" +
					"id=" + this.getId() +
					", name=" + this.getName() +
					'}';
		}


	private void sendMelDataToClouds(SimEvent ev) {
		Flow flow  = (Flow) ev.getData();
		sendNow(this.getSdnController().getId(), OsmosisTags.BUILD_ROUTE, flow);
	}
		
	public void initCloudTopology(List<ConfiguationEntity.HostEntity> hostEntites, List<ConfiguationEntity.SwitchEntity> switchEntites, List<ConfiguationEntity.LinkEntity> linkEntites) {
		 topology  = new Topology();		 
		 sdnhosts = new ArrayList<SDNHost>();
		 switches= new ArrayList<Switch>();
		 
		Hashtable<String,Integer> nameIdTable = new Hashtable<String, Integer>();
					    		    		    
		for(ConfiguationEntity.HostEntity hostEntity : hostEntites){
			long pes =  hostEntity.getPes();
			long mips = hostEntity.getMips();
			int ram = hostEntity.getRam();
			long storage = hostEntity.getStorage();					
			long bw = hostEntity.getBw(); 																				
			String hostName = hostEntity.getName();					
			Host host = createHost(OsmosisBuilder.hostId, ram, bw, storage, pes, mips);
			host.setDatacenter(this);
			SDNHost sdnHost = new SDNHost(host, hostName);
			nameIdTable.put(hostName, sdnHost.getAddress());					
			OsmosisBuilder.hostId++;					
			this.topology.addNode(sdnHost);
			this.hosts.add(host);
			this.sdnhosts.add(sdnHost);			
		}
	
		for(ConfiguationEntity.SwitchEntity switchEntity : switchEntites){
			long iops = switchEntity.getIops();
			String switchName = switchEntity.getName();
			String switchType = switchEntity.getType();
			Switch sw = null;
			sw = new Switch(switchName, switchType, iops);					
			if(sw != null) {
				nameIdTable.put(switchName, sw.getAddress());
				this.topology.addNode(sw);
				this.switches.add(sw);
			}
		}
			
		for(ConfiguationEntity.LinkEntity linkEntity : linkEntites){
				String src = linkEntity.getSource();  
				String dst = linkEntity.getDestination();				
				long bw = linkEntity.getBw();
				int srcAddress = nameIdTable.get(src);
				if(dst.equals("")){
					System.out.println("Null!");			
				}
				int dstAddress = nameIdTable.get(dst);
				topology.addLink(srcAddress, dstAddress, bw);
		}
	}

	@Override
	public void initEdgeTopology(List<EdgeDevice> devices, List<ConfiguationEntity.SwitchEntity> switchEntites,
                                 List<ConfiguationEntity.LinkEntity> linkEntites) {
		// TODO Auto-generated method stub
		
	}		
}
