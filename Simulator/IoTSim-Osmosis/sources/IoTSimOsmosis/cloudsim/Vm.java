/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package IoTSimOsmosis.cloudsim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Vm represents a VM: it runs inside a Host, sharing hostList with other VMs.
 * It processes cloudlets. This processing happens according to a policy,
 * defined by the CloudletScheduler. Each VM has a owner, which can submit
 * cloudlets to the VM to be executed
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class Vm {

	protected String vmName;
	
	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	private double mips_percore;

	private int proposedHostId;

	public int getProposedHostId() {
		return proposedHostId;
	}

	public void setProposedHostId(int proposedHostId) {
		this.proposedHostId = proposedHostId;
	}

	public double getMips_percore() {
		return mips_percore;
	}

	public void setMips_percore(double mips_percore) {
		this.mips_percore = mips_percore;
	}

	/** The id. */
	private int id;

	/** The user id. */
	private int userId;

	/** The uid. */
	private String uid;

	/** The size. */
	private double size;

	/** The MIPS. */
	private double mips;

	/** The number of PEs. */
	private int numberOfPes;

	/** The ram. */
	private int ram;

	/** The bw. */
	private double bw;

	/** The vmm. */
	private String vmm;

	/** The Cloudlet scheduler. */
	private CloudletScheduler cloudletScheduler;

	/** The host. */
	private Host host;

	/** In migration flag. */
	private boolean inMigration;

	/** The current allocated size. */
	private double currentAllocatedSize;

	/** The current allocated ram. */
	private int currentAllocatedRam;

	/** The current allocated bw. */
	private double currentAllocatedBw;

	/** The current allocated mips. */
	private List<Double> currentAllocatedMips;

	/** The VM is being instantiated. */
	private boolean beingInstantiated;

	/** The mips allocation history. */
	private final List<VmStateHistoryEntry> stateHistory = new LinkedList<VmStateHistoryEntry>();

	private int vmType;

	private int vmState = 0; // to record if this vm has been created in a host
								// successfully. 1 means successful, 0 means
								// unsuccessful

	// private double vmCostPerSec;

	public int getVmState() {
		return vmState;
	}

	public void setVmState(int vmState) {
		this.vmState = vmState;
	}

	/**
	 * Creates a new VMCharacteristics object.
	 * 
	 * @param id
	 *            unique ID of the VM
	 * @param userId
	 *            ID of the VM's owner
	 * @param mips
	 *            the mips
	 * @param numberOfPes
	 *            amount of CPUs
	 * @param ram
	 *            amount of ram
	 * @param bw2
	 *            amount of bandwidth
	 * @param size2
	 *            amount of storage
	 * @param vmm
	 *            virtual machine monitor
	 * @param cloudletScheduler
	 *            cloudletScheduler policy for cloudlets
	 * @pre id >= 0
	 * @pre userId >= 0
	 * @pre size > 0
	 * @pre ram > 0
	 * @pre bw > 0
	 * @pre cpus > 0
	 * @pre priority >= 0
	 * @pre cloudletScheduler != null
	 * @post $none
	 */
	public Vm(int id, int userId, double mips, int numberOfPes, int ram,
			double bw2, double size2, String vmm,
			CloudletScheduler cloudletScheduler, int vmType) {
		setId(id);
		setUserId(userId);
		setUid(getUid(userId, id));
		setMips(mips);
		setNumberOfPes(numberOfPes);
		setRam(ram);
		setBw(bw2);
		setSize(size2);
		setVmm(vmm);
		setCloudletScheduler(cloudletScheduler);
		// setvmCostPerSec(vmType);
		this.vmType = vmType;

		setInMigration(false);
		setBeingInstantiated(true);

		setCurrentAllocatedBw(0);
		setCurrentAllocatedMips(null);
		setCurrentAllocatedRam(0);
		setCurrentAllocatedSize(0);
	}

	public Vm(int id, int userId, double mips2, int numberOfPes, int ram,
			double bw, double size, String vmm,
			CloudletScheduler cloudletScheduler) {
		setId(id);
		setUserId(userId);
		setUid(getUid(userId, id));
		setMips(mips2);
		setNumberOfPes(numberOfPes);
		setRam(ram);
		setBw(bw);
		setSize(size);
		setVmm(vmm);
		setCloudletScheduler(cloudletScheduler);
		// setvmCostPerSec(vmType);
		// this.vmType = vmType;

		setInMigration(false);
		setBeingInstantiated(true);

		setCurrentAllocatedBw(0);
		setCurrentAllocatedMips(null);
		setCurrentAllocatedRam(0);
		setCurrentAllocatedSize(0);
	}
	
	public Vm(int id, int userId, double mips2, int numberOfPes, int ram,
			double bw, double size, String vmm,
			CloudletScheduler cloudletScheduler, Host host) {
		setId(id);
		setUserId(userId);
		setUid(getUid(userId, id));
		setMips(mips2);
		setNumberOfPes(numberOfPes);
		setRam(ram);
		setBw(bw);
		setSize(size);
		setVmm(vmm);
		setCloudletScheduler(cloudletScheduler);
		// setvmCostPerSec(vmType);
		// this.vmType = vmType;

		setInMigration(false);
		setBeingInstantiated(true);

		setCurrentAllocatedBw(0);
		setCurrentAllocatedMips(null);
		setCurrentAllocatedRam(0);
		setCurrentAllocatedSize(0);
		setHost(host);
	}

	/*
	 * private void setvmCostPerSec(int vmType) { if(vmType == 1) vmCostPerSec =
	 * 3.0; else if (vmType ==2) vmCostPerSec = 5.0; else vmCostPerSec = 7.0;
	 * 
	 * }
	 */

	public String getvmType() {
		if (vmType == 1)
			return "Small";
		else if (vmType == 2)
			return "Medium";
		else
			return "Large";

	}

	/**
	 * Updates the processing of cloudlets running on this VM.
	 * 
	 * @param currentTime
	 *            current simulation time
	 * @param mipsShare
	 *            array with MIPS share of each Pe available to the scheduler
	 * @return time predicted completion time of the earliest finishing
	 *         cloudlet, or 0 if there is no next events
	 * @pre currentTime >= 0
	 * @post $none
	 */
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		if (mipsShare != null) {
			return getCloudletScheduler().updateVmProcessing(currentTime,
					mipsShare);
		}
		return 0.0;
	}

	/**
	 * Gets the current requested mips.
	 * 
	 * @return the current requested mips
	 */
	public List<Double> getCurrentRequestedMips() {
		List<Double> currentRequestedMips = getCloudletScheduler()
				.getCurrentRequestedMips();
		if (isBeingInstantiated()) {
			currentRequestedMips = new ArrayList<Double>();
			for (int i = 0; i < getNumberOfPes(); i++) {
				currentRequestedMips.add(getMips());
			}
		}
		return currentRequestedMips;
	}

	/**
	 * Gets the current requested total mips.
	 * 
	 * @return the current requested total mips
	 */
	public double getCurrentRequestedTotalMips() {
		double totalRequestedMips = 0;
		for (double mips : getCurrentRequestedMips()) {
			totalRequestedMips += mips;
		}
		return totalRequestedMips;
	}

	/**
	 * Gets the current requested max mips among all virtual PEs.
	 * 
	 * @return the current requested max mips
	 */
	public double getCurrentRequestedMaxMips() {
		double maxMips = 0;
		for (double mips : getCurrentRequestedMips()) {
			if (mips > maxMips) {
				maxMips = mips;
			}
		}
		return maxMips;
	}

	/**
	 * Gets the current requested bw.
	 * 
	 * @return the current requested bw
	 */
	public double getCurrentRequestedBw() {
		if (isBeingInstantiated()) {
			return getBw();
		}
		return (double) (getCloudletScheduler()
				.getCurrentRequestedUtilizationOfBw() * getBw());
	}

	/**
	 * Gets the current requested ram.
	 * 
	 * @return the current requested ram
	 */
	public int getCurrentRequestedRam() {
		if (isBeingInstantiated()) {
			return getRam();
		}
		return (int) (getCloudletScheduler()
				.getCurrentRequestedUtilizationOfRam() * getRam());
	}

	/**
	 * Get utilization created by all clouddlets running on this VM.
	 * 
	 * @param time
	 *            the time
	 * @return total utilization
	 */
	public double getTotalUtilizationOfCpu(double time) {
		return getCloudletScheduler().getTotalUtilizationOfCpu(time);
	}

	/**
	 * Get utilization created by all cloudlets running on this VM in MIPS.
	 * 
	 * @param time
	 *            the time
	 * @return total utilization
	 */
	public double getTotalUtilizationOfCpuMips(double time) {
		return getTotalUtilizationOfCpu(time) * getMips();
	}

	/**
	 * Sets the uid.
	 * 
	 * @param uid
	 *            the new uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Get unique string identificator of the VM.
	 * 
	 * @return string uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Generate unique string identificator of the VM.
	 * 
	 * @param userId
	 *            the user id
	 * @param vmId
	 *            the vm id
	 * @return string uid
	 */
	public static String getUid(int userId, int vmId) {
		return userId + "-" + vmId;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param userId
	 *            the new user id
	 */
	protected void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Gets the ID of the owner of the VM.
	 * 
	 * @return VM's owner ID
	 * @pre $none
	 * @post $none
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Gets the mips.
	 * 
	 * @return the mips
	 */
	public double getMips() {
		return mips;
	}

	/**
	 * Sets the mips.
	 * 
	 * @param mips
	 *            the new mips
	 */
	protected void setMips(double mips) {
		this.mips = mips;
	}

	/**
	 * Gets the number of pes.
	 * 
	 * @return the number of pes
	 */
	public int getNumberOfPes() {
		return numberOfPes;
	}

	/**
	 * Sets the number of pes.
	 * 
	 * @param numberOfPes
	 *            the new number of pes
	 */
	protected void setNumberOfPes(int numberOfPes) {
		this.numberOfPes = numberOfPes;
	}

	/**
	 * Gets the amount of ram.
	 * 
	 * @return amount of ram
	 * @pre $none
	 * @post $none
	 */
	public int getRam() {
		return ram;
	}

	/**
	 * Sets the amount of ram.
	 * 
	 * @param ram
	 *            new amount of ram
	 * @pre ram > 0
	 * @post $none
	 */
	public void setRam(int ram) {
		this.ram = ram;
	}

	/**
	 * Gets the amount of bandwidth.
	 * 
	 * @return amount of bandwidth
	 * @pre $none
	 * @post $none
	 */
	public double getBw() {
		return bw;
	}

	/**
	 * Sets the amount of bandwidth.
	 * 
	 * @param bw
	 *            new amount of bandwidth
	 * @pre bw > 0
	 * @post $none
	 */
	public void setBw(double bw) {
		this.bw = bw;
	}

	/**
	 * Gets the amount of storage.
	 * 
	 * @return amount of storage
	 * @pre $none
	 * @post $none
	 */
	public double getSize() {
		return size;
	}

	/**
	 * Sets the amount of storage.
	 * 
	 * @param size
	 *            new amount of storage
	 * @pre size > 0
	 * @post $none
	 */
	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * Gets the VMM.
	 * 
	 * @return VMM
	 * @pre $none
	 * @post $none
	 */
	public String getVmm() {
		return vmm;
	}

	/**
	 * Sets the VMM.
	 * 
	 * @param vmm
	 *            the new VMM
	 */
	protected void setVmm(String vmm) {
		this.vmm = vmm;
	}

	/**
	 * Sets the host that runs this VM.
	 * 
	 * @param host
	 *            Host running the VM
	 * @pre host != $null
	 * @post $none
	 */
	public void setHost(Host host) {
		this.host = host;
	}

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public Host getHost() {
		return host;
	}

	/**
	 * Gets the vm scheduler.
	 * 
	 * @return the vm scheduler
	 */
	public CloudletScheduler getCloudletScheduler() {
		return cloudletScheduler;
	}

	/**
	 * Sets the vm scheduler.
	 * 
	 * @param cloudletScheduler
	 *            the new vm scheduler
	 */
	protected void setCloudletScheduler(CloudletScheduler cloudletScheduler) {
		this.cloudletScheduler = cloudletScheduler;
	}

	/**
	 * Checks if is in migration.
	 * 
	 * @return true, if is in migration
	 */
	public boolean isInMigration() {
		return inMigration;
	}

	/**
	 * Sets the in migration.
	 * 
	 * @param inMigration
	 *            the new in migration
	 */
	public void setInMigration(boolean inMigration) {
		this.inMigration = inMigration;
	}

	/**
	 * Gets the current allocated size.
	 * 
	 * @return the current allocated size
	 */
	public double getCurrentAllocatedSize() {
		return currentAllocatedSize;
	}

	/**
	 * Sets the current allocated size.
	 * 
	 * @param currentAllocatedSize
	 *            the new current allocated size
	 */
	protected void setCurrentAllocatedSize(double currentAllocatedSize) {
		this.currentAllocatedSize = currentAllocatedSize;
	}

	/**
	 * Gets the current allocated ram.
	 * 
	 * @return the current allocated ram
	 */
	public int getCurrentAllocatedRam() {
		return currentAllocatedRam;
	}

	/**
	 * Sets the current allocated ram.
	 * 
	 * @param currentAllocatedRam
	 *            the new current allocated ram
	 */
	public void setCurrentAllocatedRam(int currentAllocatedRam) {
		this.currentAllocatedRam = currentAllocatedRam;
	}

	/**
	 * Gets the current allocated bw.
	 * 
	 * @return the current allocated bw
	 */
	public double getCurrentAllocatedBw() {
		return currentAllocatedBw;
	}

	/**
	 * Sets the current allocated bw.
	 * 
	 * @param currentAllocatedBw
	 *            the new current allocated bw
	 */
	public void setCurrentAllocatedBw(double currentAllocatedBw) {
		this.currentAllocatedBw = currentAllocatedBw;
	}

	/**
	 * Gets the current allocated mips.
	 * 
	 * @return the current allocated mips
	 * @TODO replace returning the field by a call to
	 *       getCloudletScheduler().getCurrentMipsShare()
	 */
	public List<Double> getCurrentAllocatedMips() {
		return currentAllocatedMips;
	}

	/**
	 * Sets the current allocated mips.
	 * 
	 * @param currentAllocatedMips
	 *            the new current allocated mips
	 */
	public void setCurrentAllocatedMips(List<Double> currentAllocatedMips) {
		this.currentAllocatedMips = currentAllocatedMips;
	}

	/**
	 * Checks if is being instantiated.
	 * 
	 * @return true, if is being instantiated
	 */
	public boolean isBeingInstantiated() {
		return beingInstantiated;
	}

	/**
	 * Sets the being instantiated.
	 * 
	 * @param beingInstantiated
	 *            the new being instantiated
	 */
	public void setBeingInstantiated(boolean beingInstantiated) {
		this.beingInstantiated = beingInstantiated;
	}

	/**
	 * Gets the state history.
	 * 
	 * @return the state history
	 */
	public List<VmStateHistoryEntry> getStateHistory() {
		return stateHistory;
	}

	/**
	 * Adds the state history entry.
	 * 
	 * @param time
	 *            the time
	 * @param allocatedMips
	 *            the allocated mips
	 * @param requestedMips
	 *            the requested mips
	 * @param isInMigration
	 *            the is in migration
	 */
	public void addStateHistoryEntry(double time, double allocatedMips,
			double requestedMips, boolean isInMigration) {
		VmStateHistoryEntry newState = new VmStateHistoryEntry(time,
				allocatedMips, requestedMips, isInMigration);
		if (!getStateHistory().isEmpty()) {
			VmStateHistoryEntry previousState = getStateHistory().get(
					getStateHistory().size() - 1);
			if (previousState.getTime() == time) {
				getStateHistory().set(getStateHistory().size() - 1, newState);
				return;
			}
		}
		getStateHistory().add(newState);
	}

}
