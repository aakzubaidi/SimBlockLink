package IoTSimOsmosis.osmosis.core;

import IoTSimOsmosis.cloudsim.Host;
import IoTSimOsmosis.cloudsim.VmAllocationPolicy;

import java.util.List;

public interface VmAllocationPolicyFactory {
    // VmAllocationPolicy represents the type  and it is a return type, e.g., int
    // we are passing a list of hosts to the policy so that VMs are allocated to servers while energy policy is applied
    VmAllocationPolicy create(List<? extends Host> list);
}
