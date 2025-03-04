/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */

package IoTSimOsmosis.cloudsim.sdn.example.policies;

import java.util.List;

import IoTSimOsmosis.cloudsim.Host;
import IoTSimOsmosis.cloudsim.Vm;

/**
 * VM Allocation Policy - Only compute power, LFF.
 * When select a host to create a new VM, this policy chooses 
 * the least full host in terms of compute power (MIPS) only.   
 *  
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */
public class VmAllocationPolicyMipsLeastFullFirst extends VmAllocationPolicyCombinedMostFullFirst{

	public VmAllocationPolicyMipsLeastFullFirst(List<? extends Host> list) {
		super(list);
	}

	/**
	 * Allocates a host for a given VM.
	 * 
	 * @param vm VM specification
	 * @return $true if the host could be allocated; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	@Override
	public boolean allocateHostForVm(Vm vm) {
		if (getVmTable().containsKey(vm.getUid())) { // if this vm was not created
			return false;
		}
		
		int numHosts = getHostList().size();

		// 1. Find/Order the best host for this VM by comparing a metric
		int requiredPes = vm.getNumberOfPes();
		double requiredMips = vm.getCurrentRequestedTotalMips();
		long requiredBw = (long) vm.getCurrentRequestedBw();

		boolean result = false;
		
		double[] freeResources = new double[numHosts];
		for (int i = 0; i < numHosts; i++) {
			double mipsFreePercent = (double)getFreeMips().get(i) / hostTotalMips; 
			//double bwFreePercent = (double)getFreeBw().get(i) / hostTotalBw;
			
			freeResources[i] = mipsFreePercent;
		}

		for(int tries = 0; tries < numHosts; tries++) {// we still trying until we find a host or until we try all of them
			double moreFree = Double.NEGATIVE_INFINITY;
			int idx = -1;

			// we want the host with less pes in use
			for (int i = 0; i < numHosts; i++) {
				if (freeResources[i] > moreFree) {
					moreFree = freeResources[i];
					idx = i;
				}
			}
			
			if(idx==-1) {
				System.err.println("Cannot assign the VM to any host:"+tries+"/"+numHosts);
				return false;
			}
			
			freeResources[idx] = Double.NEGATIVE_INFINITY;
			
			Host host = getHostList().get(idx);
			
			// Check whether the host can hold this VM or not.
			if(getFreeMips().get(idx) < requiredMips ||
					getFreeBw().get(idx) < requiredBw ||
					getFreePes().get(idx) < requiredPes) {
				//Cannot host the VM
				continue;
			}
			
			result = host.vmCreate(vm);

			if (result) { // if vm were succesfully created in the host
				getVmTable().put(vm.getUid(), host);
				getUsedPes().put(vm.getUid(), requiredPes);
				getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
				
				getUsedMips().put(vm.getUid(), (long) requiredMips);
				getFreeMips().set(idx,  (long) (getFreeMips().get(idx) - requiredMips));

				getUsedBw().put(vm.getUid(), (long) requiredBw);
				getFreeBw().set(idx,  (long) (getFreeBw().get(idx) - requiredBw));
				break;
			} 
		}
		logMaxNumHostsUsed();
		return result;
	}

}

