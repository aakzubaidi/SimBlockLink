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

package IoTSimOsmosis.cloudsim.edge.iot;

import IoTSimOsmosis.cloudsim.edge.iot.network.EdgeNetworkInfo;
import IoTSimOsmosis.cloudsim.core.SimEvent;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class CarSensor extends IoTDevice {
	
	public CarSensor(EdgeNetworkInfo networkModel, String name, double bandwidth) {
		super(name, networkModel, bandwidth);		
	}

	@Override
	public boolean updateBatteryBySensing() {
		battery.setCurrentCapacity(battery.getCurrentCapacity() - battery.getBatterySensingRate());
		if(battery.getCurrentCapacity()<0)
			return  true;
		return false;
	}

	@Override
	public boolean updateBatteryByTransmission() {
		battery.setCurrentCapacity(battery.getCurrentCapacity() - battery.getBatterySendingRate());
		if(battery.getCurrentCapacity()<0)
			return  true;
		return false;
	}

	@Override
	public void startEntity() {
		super.startEntity();
	}

	@Override
	public void processEvent(SimEvent ev) {
		super.processEvent(ev);
	}

	@Override
	public void shutdownEntity() {
	}
}
