package IoTSimOsmosis.osmosis.core.polocies;

import IoTSimOsmosis.cloudsim.edge.core.edge.Mobility;

public class SimpleMovingPolicy implements MovingPolicy{

	/**
	 * moving a straight line, when reach the end, it will reverse the direction.
	 */
	@Override
	public void updateLocation(Mobility mobility) {

		Mobility.Location location = mobility.location;
		location.x += mobility.velocity;
		mobility.totalMovingDistance+=Math.abs(mobility.velocity);
		if((location.x>=mobility.range.endX) || (location.x<=mobility.range.beginX)) {
			mobility.velocity=-mobility.velocity;
			if(location.x>mobility.range.endX) {
				location.x=mobility.range.endX;
			}
			if(location.x<mobility.range.beginX) {
				location.x=mobility.range.beginX;
			}

		}

	}

}
