/*
 * Title:         IoTSim-SDWAN 1.0
 * Description:  Ifacilitates the modeling, simulating, and evaluating of new algorithms, policies, and designs in the context of SD-WAN ecosystems and SDN-enabled multiple cloud datacenters.
 * 			     
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2020, Newcastle University (UK) and Saudi Electronic University (Saudi Arabia) 
 * 
 */

package IoTSimOsmosis.osmosis.core;

import java.util.*;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class OsmesisAppsParserDynmic {
//	public static List<OsmesisAppDescription> appList = new ArrayList<>();
//
//
//	public static void startParsingExcelAppFile() {
//
//		String OsmesisAppName = "App1"; // assume we have only one app for now
//		int appID = 1; // assume we have only one app for now
//		int NumOfLayer = 3; // IoT layer --> edge datacenter --> cloud datacenter. You can add more layers!
//		double DataRate = 1.2; // in seconds
//		double StopDataGenerationTime = 300; // in seconds
//
//		int numberOfCloudDatacenter = 1;
//		int numberOfEdgeDatacenter = 4;
//
//		/*
//		 IoT device, MEL 1, etc.
//		 There is a class in this class that take care of adding dynamic number of IoT devices
//		 */
//		String sourceName;
//		int iotTotalNumberPerEDC = 1000; // number of IoT devices per edge datacenter
//		int iotTotalNumbers = 4000; // number of IoT devices in all edge datacenters
//		int iotCount = 1;
//	/*
//		IoT device, MEL 1, etc.
//		There is a class in this class that take care of adding dynamic number of edge/MEL devices
//	 */
//		String destName;
//		int melTotalNumberPerEDC = 0; // number of MEL devices per datacenter
//		int melTotalNumbers = 2000; // number of MEL devices in all edge datacenters
//		int melCount = 1;
//
//
//
//		long osmoticLetSize; // source MEL processing size
//		long osmoticPktSize; // source MEL transmission size
//
//		String sourceLayerName; // IoT layer, edge datacenter 1, etc.
//		String destLayerName; //  IoT layer, edge datacenter 1, etc.
//
//		List<OsmosisLayer> layers = new ArrayList<>(); // NumOfLayer (IoT, edges, clouds)
//
//		for (int i = 1; i <= NumOfLayer; i++) {
//			for(int edgeDatacenter = 1; edgeDatacenter <= numberOfEdgeDatacenter; edgeDatacenter++) {
//				melTotalNumberPerEDC += 500; // number of MEL devices per datacenter
//
//				for(int IoTDevices = 1; IoTDevices <= iotTotalNumberPerEDC; IoTDevices++) {
//					sourceName = "iotDevice_" + iotCount;
//					osmoticLetSize = 50; // MI
//					osmoticPktSize = 90; // Mb
//
//					if(IoTDevices % 2 == 0){
//						melCount++;
//					}
//
//					destName = "MEL_" + melCount;
//
//					sourceLayerName = "IoT_" + ;
//					destLayerName = "Edge_" + edgeDatacenter;
//					OsmosisLayer layer = new OsmosisLayer(sourceName, sourceLayerName, osmoticLetSize, osmoticPktSize, destName, destLayerName);
//					layers.add(layer);
//
//					sourceLayerName = destLayerName; // IoT, edge datacenter 1, etc.
//					iotCount++;
//				}
//			}
//
//			if (i == NumOfLayer) {
//				osmoticLetSize = Long.parseLong(lineitems.poll());
//				OsmosisLayer layer = new OsmosisLayer(sourceName, sourceLayerName, osmoticLetSize, 0, null, null);
//				layers.add(layer);
//				break;
//			}
//
//			osmoticLetSize = Long.parseLong(lineitems.poll());
//			osmoticPktSize = Long.parseLong(lineitems.poll());
//			destName = lineitems.poll(); // IoT device, mel 1, mel 2, etc.
//			destLayerName = lineitems.poll(); // IoT, edge datacenter 1, etc.
//			OsmosisLayer layer = new OsmosisLayer(sourceName, sourceLayerName, osmoticLetSize, osmoticPktSize, destName, destLayerName);
//			layers.add(layer);
//			sourceName = destName; // IoT device, mel 1, mel 2, etc.
//			sourceLayerName = destLayerName; // IoT, edge datacenter 1, etc.
//		}
//
//		OsmesisAppDescription appComposition = new OsmesisAppDescription(OsmesisAppName, appID, DataRate, StopDataGenerationTime,
//				layers);
//
//		appList.add(appComposition);
//
//	}
//
//	public void setInfo(int addOneMore){
//		sourceName = "IoTDevice_" + IoTDevices;
//		sourceName = destName; // IoT device, mel 1, mel 2, etc.
//	}

}

