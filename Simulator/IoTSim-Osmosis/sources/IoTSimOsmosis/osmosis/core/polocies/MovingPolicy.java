package IoTSimOsmosis.osmosis.core.polocies;

import IoTSimOsmosis.cloudsim.edge.core.edge.Mobility;

public interface MovingPolicy {
	public void updateLocation(Mobility mobility);
}
