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

package IoTSimOsmosis.cloudsim.edge.iot.network;

import IoTSimOsmosis.cloudsim.edge.iot.protocol.IoTProtocol;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class EdgeNetworkInfo {

    private EdgeNetwork networkType; // wifi, etc. 
    private IoTSimOsmosis.cloudsim.edge.iot.protocol.IoTProtocol IoTProtocol; // XMPP, etc.

	public EdgeNetworkInfo(EdgeNetwork networkType, IoTProtocol IoTProtocol) {
		this.networkType = networkType;
		this.IoTProtocol = IoTProtocol;
    }

	public IoTProtocol getIoTProtocol() {
		return IoTProtocol;
	}

    public EdgeNetwork getNetWorkType() {
        return networkType;
    }
}