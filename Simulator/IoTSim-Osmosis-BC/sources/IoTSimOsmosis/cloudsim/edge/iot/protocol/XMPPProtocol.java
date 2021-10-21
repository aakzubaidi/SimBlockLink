package IoTSimOsmosis.cloudsim.edge.iot.protocol;


public class XMPPProtocol extends IoTProtocol {
	private static final float BATTERY_DRAINAGE_RATE=1.50f;
	private static final float TRANSIMISON_SPEED=3.00f;

    public XMPPProtocol() {
    		super("XMPP",BATTERY_DRAINAGE_RATE,TRANSIMISON_SPEED);
    }

}