package IoTSimOsmosis.osmosis.core.Infrastructure;

import java.util.ArrayList;
import java.util.Map;

public class EdgeDatacenters {

    private String name;
    private  String type;
    private double schedulingInterval;
    private String melAllocationPolicy; // mel or VM, same thing
    private ArrayList<String> characteristics;
    private Map<Integer,String> edgeDevices; // ID, characteristics
    private Map<Integer,String> MELEntities; // ID, characteristics
    private Map<Integer,String> controllers; // ID, characteristics
    private Map<Integer,String> switches; // ID, characteristics
    private Map<Integer,String> links; // network links

    public EdgeDatacenters(String name, String type, double schedulingInterval) {
        this.name = name;
        this.type = type;
        this.schedulingInterval = schedulingInterval;
    }

    public void addMelAllocationPolicy(String melAllocationPolicy) {
        this.melAllocationPolicy = melAllocationPolicy;
    }

    public void setCharacteristics(double timeZone) {
    /*
        "costPerMem":0.05,
        "cost":1.0,
        "os":"Linux",
        "costPerSec":0.0,
        "vmm":"Xen",
        "timeZone":10.0,
        "costPerBw":0.0,
        "costPerStorage":0.001,
        "architecture":"x86"
     */

        this.characteristics = characteristics;
    }

    public void addEdgeDevices(Map<Integer, String> edgeDevices) {
        this.edgeDevices = edgeDevices;
    }

    public void addMELEntities(Map<Integer, String> MELEntities) {
        this.MELEntities = MELEntities;
    }

    public void addControllers(Map<Integer, String> controllers) {
        this.controllers = controllers;
    }

    public void addSwitches(Map<Integer, String> switches) {
        this.switches = switches;
    }

    public void addLinks(Map<Integer, String> links) {
        this.links = links;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    public String getMelAllocationPolicy() {
        return melAllocationPolicy;
    }

    public ArrayList<String> getCharacteristics() {
        return characteristics;
    }

    public Map<Integer, String> getEdgeDevices() {
        return edgeDevices;
    }

    public Map<Integer, String> getMELEntities() {
        return MELEntities;
    }

    public Map<Integer, String> getControllers() {
        return controllers;
    }

    public Map<Integer, String> getSwitches() {
        return switches;
    }

    public Map<Integer, String> getLinks() {
        return links;
    }


}
