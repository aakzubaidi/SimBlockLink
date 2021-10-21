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

package IoTSimOsmosis.cloudsim.osmesis.examples.uti;


import IoTSimOsmosis.cloudsim.Log;
import IoTSimOsmosis.cloudsim.edge.core.edge.EdgeLet;
import IoTSimOsmosis.osmosis.core.*;
import application.java.Agent;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class PrintResults_Example_3 {
	GlobalVariable globalVariable = GlobalVariable.getInstance();
	Agent agent;
	
		
	public void printOsmesisNetwork() {
		
		List<WorkflowInfo> tags = new ArrayList<>();
		for(OsmesisAppDescription app : OsmesisAppsParser.appList){
			for(WorkflowInfo workflowTag : OsmesisBroker.workflowTag){
				workflowTag.getAppId();
				if(app.getAppID() == workflowTag.getAppId()){
					tags.add(workflowTag);
				}
			}
			printOsmesisApp(tags);		
			tags.clear();
		}
		
		Log.printLine();				
		Log.printLine("=========================== Osmesis Overall Apps Results ========================");
		Log.printLine(String.format("%1s %19s %32s %16s %14s %19s %25s"
				, "App_Name"
				, "IoTDeviceDrained"
				, "IoTDeviceBatteryConsumption"
				, "StartTime" 
				, "EndTime" 
				, "SimluationTime"
				, "appTotalRunningTmie"));

		for(OsmesisAppDescription app : OsmesisAppsParser.appList){
			for(WorkflowInfo workflowTag : OsmesisBroker.workflowTag){
				workflowTag.getAppId();
				if(app.getAppID() == workflowTag.getAppId()){
					tags.add(workflowTag);
				}
			}			
			printAppStat(app, tags);
			tags.clear();
		}
//		printAppWorkflowConfigration();
	}
	
//	private void printAppWorkflowConfigration() {
//		Log.printLine();
//		Log.printLine("=========================== Osmesis Workflow Configrations ========================");
//		Log.printLine(String.format("%1s %17s %26s  %13s  %30s %13s %21s %23s %12s %21s"
//				,"App_Name"
//				, "DataRate_Sec"
//				, "StopDataGeneration_Sec"
//				, "IoTDevice"
//				, "IoTDeviceOutputData_Mb"
//				, "MELName"
//				, "OsmesisEdgelet_MI"
//				, "MELOutputData_Mb"
//				, "VmName"
//				, "OsmesisCloudlet_MI"
//				));
//		for(OsmesisAppDescription app : OsmesisAppsParser.appList){
//			Log.printLine(String.format("%1s %15s %21s  %25s  %18s %22s %15s %24s %18s %14s"
//					, app.getAppName()
//					, app.getDataRate()
//					, app.getStopDataGenerationTime()
//					, app.getIoTDeviceName()
//					, app.getIoTDeviceOutputSize()
//					, app.getMELName()
//					, app.getOsmesisEdgeletSize()
//					, app.getMELOutputSize()
//					, app.getVmName()
//					, app.getOsmesisCloudletSize()
//					));
//		}
//	}

	private void printAppStat(OsmesisAppDescription app, List<WorkflowInfo> tags) {		 	
		String appName = app.getAppName();
		String isIoTDeviceDrained = app.getIoTDeviceBatteryStatus();
		double iotDeviceTotalConsumption = app.getIoTDeviceBatteryConsumption();
//		long TotalIoTGeneratedData = 0;
//		long TotalEdgeLetSizes = 0;
//		long TotalMELGeneratedData = 0;
//		long TotalCloudLetSizes = 0;
		double StartTime = 0;
		double EndTime = 0;
		double SimluationTime = 0;
		double appTotalRunningTmie = 0;




        StartTime = app.getAppStartTime();
        int transactionListSize = tags.size()-1;
        int lastOsmosisLet = tags.get(transactionListSize).getOsmosisLetSize();
        EndTime = tags.get(transactionListSize).getOsmosislet(lastOsmosisLet-1).getFinishTime();
        SimluationTime = EndTime - StartTime;
		
		WorkflowInfo firstWorkflow = tags.get(0);
		WorkflowInfo secondWorkflow = tags.get(1);
		
		if(firstWorkflow.getFinishTime() > secondWorkflow.getSartTime()){
			appTotalRunningTmie = EndTime - StartTime;			
		} else {
			for(WorkflowInfo workflowTag : tags){
				appTotalRunningTmie += workflowTag.getFinishTime() - workflowTag.getSartTime(); 
			}
		}
		
//		for(WorkflowInfo workflowTag : tags){
//			TotalIoTGeneratedData += workflowTag.getIotDeviceFlow().getSize();
//			TotalEdgeLetSizes += workflowTag.getEdgeLet().getCloudletLength();
//			TotalMELGeneratedData += workflowTag.getEdgeToCloudFlow().getSize();
//			TotalCloudLetSizes += workflowTag.getCloudLet().getCloudletLength();
//		}

		Log.printLine(String.format("%1s %15s %28s %24s %17s %15s %22s"
				, appName
				, isIoTDeviceDrained
				,  new DecimalFormat("0.00").format(iotDeviceTotalConsumption)
				,  new DecimalFormat("0.000").format(StartTime) 
				,  new DecimalFormat("0.000").format(EndTime) 
				,  new DecimalFormat("0.000").format(SimluationTime)
				,  new DecimalFormat("0.000").format(appTotalRunningTmie)				
				));
	}
	
	public double detemineMaxValue(double oldValue, double newValue){
		if(oldValue < newValue){
			oldValue  = newValue;	
		}
		return oldValue;
	}

	public void printNetworkStatistics(Flow flow){				
			Log.printLine(String.format("%1s %21s %12s %19s %15s %12s %11s %22s %16s %22s "
					, flow.getFlowId()
					, "" 					
					, flow.getAppName() 
					, ""
					, flow.getAppNameSrc() 
					, flow.getAppNameDest()
					, flow.getSize()  					
					, new DecimalFormat("0.0000").format(flow.getStartTime()) 
					, new DecimalFormat("0.0000").format(flow.getFinishTime() -  flow.getStartTime())   
					, new DecimalFormat("0.0000").format(flow.getFinishTime())));		
	}
	
	public void printOsmesisApp(List<WorkflowInfo> tags) {
		try {
			agent = new Agent(globalVariable.getManager(), globalVariable.getQoS().get(0)) ;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.printLine();				
		Log.printLine("=========================== Osmesis App Results ========================");
		Log.printLine(String.format("%1s %11s %18s %17s %17s %19s %20s %35s %37s %21s %29s %29s %33s %32s %23s %28s %20s %30s %33s %23s %28s %20s %30s %25s %10s"
				,"App_ID"
				,"AppName"
				,"Transaction"
				,"StartTime"
				,"FinishTime"
				,"IoTDeviceName"
				,"MELName"
				,"DataSizeIoTDeviceToMEL_Mb"  
				,"TransmissionTimeIoTDeviceToMEL"
				,"EdgeLetMISize"
				,"EdgeLet_MEL_StartTime"
				,"EdgeLet_MEL_FinishTime"
				,"EdgeLetProccessingTimeByMEL"				  

				,"DestinationVmName_Cloud1"
				,"DataSizeMELToVM_Mb"
				,"TransmissionTimeMELToVM"  
				,"CloudLetMISize"
				,"CloudLetProccessingTimeByVM"

				,"DestinationVmName_Cloud2"
				,"DataSizeMELToVM_Mb"
				,"TransmissionTimeMELToVM"
				,"CloudLetMISize"
				,"CloudLetProccessingTimeByVM"

				, "TransactionTotalTime"
				, "   "));

        double transactionTransmissionTime = 0;
        double transactionOsmosisLetTime = 0;

        double transactionTotalTime;
        for(WorkflowInfo workflowTag : tags){
            transactionTransmissionTime = 0;
            transactionOsmosisLetTime = 0;
            transactionTotalTime = 0;

            for(int i = 0; i < workflowTag.getFlowLists().size(); i++){
                Flow flow = workflowTag.getOsmosisFlow(i);
                transactionTransmissionTime += flow.getTransmissionTime();
            }

            for(int x =0; x < workflowTag.getOsmosisLetSize(); x++){
                EdgeLet let = workflowTag.getOsmosislet(x);
                transactionOsmosisLetTime += let.getActualCPUTime();
            }
            transactionTotalTime = transactionTransmissionTime +  transactionOsmosisLetTime;
            try {
				//System.out.println("<<>><<>>counter<<>><<>>: "+ counter);
				agent.evaluateGeneratedMetric(transactionTotalTime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.printLine(String.format("%1s %15s %15s %18s %18s %22s %25s %18s %34s %32s %24s %28s %31s %41s %18s %26s %23s %24s %44s %18s %22s %28s %23s  %28s"
					, workflowTag.getAppId()
					, workflowTag.getAppName()
					, workflowTag.getWorkflowId()	
					, new DecimalFormat("0.00").format(workflowTag.getSartTime())
					, new DecimalFormat("0.00").format(workflowTag.getFinishTime())
					, workflowTag.getOsmosisFlow(0).getAppNameSrc()
					, workflowTag.getOsmosisFlow(0).getAppNameDest() + " (" +workflowTag.getDCName(0) + ")"
					, workflowTag.getOsmosisFlow(0).getSize()
					, new DecimalFormat("0.00").format(workflowTag.getOsmosisFlow(0).getTransmissionTime())
					, workflowTag.getOsmosislet(0).getCloudletLength()
					, new DecimalFormat("0.00").format(workflowTag.getOsmosislet(0).getExecStartTime())
					, new DecimalFormat("0.00").format(workflowTag.getOsmosislet(0).getFinishTime())
					, new DecimalFormat("0.00").format(workflowTag.getOsmosislet(0).getActualCPUTime())
					, workflowTag.getOsmosisFlow(1).getAppNameDest() + " (" +workflowTag.getDCName(1) + ")"
					, workflowTag.getOsmosisFlow(1).getSize()
					, new DecimalFormat("0.00").format(workflowTag.getOsmosisFlow(1).getTransmissionTime())
					, workflowTag.getOsmosislet(1).getCloudletLength()
					, new DecimalFormat("0.00").format(workflowTag.getOsmosislet(1).getActualCPUTime())

					, workflowTag.getOsmosisFlow(2).getAppNameDest() + " (" +workflowTag.getDCName(2) + ")"
					, workflowTag.getOsmosisFlow(2).getSize()
					, new DecimalFormat("0.00").format(workflowTag.getOsmosisFlow(2).getTransmissionTime())
					, workflowTag.getOsmosislet(2).getCloudletLength()
					, new DecimalFormat("0.00").format(workflowTag.getOsmosislet(2).getActualCPUTime())


					, new DecimalFormat("0.00").format(transactionTotalTime)));
		}
	}
}
