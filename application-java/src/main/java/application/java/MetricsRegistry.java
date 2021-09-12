package application.java;

import java.util.HashMap;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public class MetricsRegistry {

    HashMap<String, Counter> CounterRegistry;
    HashMap<String, Histogram> HistogramRegistry;



    MetricsRegistry ()
    {
        CounterRegistry = new HashMap<String, Counter>();
        HistogramRegistry = new HashMap<String, Histogram>();
    }

    public Counter[] getCounter (String smartContractMethod) {

        Counter sucessCounter = null;
        Counter failCounter = null;
        if(CounterRegistry.get(smartContractMethod+"_sucess") == null || CounterRegistry.get(smartContractMethod+"_fail") == null)
        {
            sucessCounter = Counter.build().name(smartContractMethod+"_sucess").help("Sucessful Transaction").register();
            failCounter = Counter.build().name(smartContractMethod+"_fail").help("Failed Transaction").register();
            CounterRegistry.put(smartContractMethod+"_sucess", sucessCounter);
            CounterRegistry.put(smartContractMethod+"_fail", failCounter);
        }
         sucessCounter = CounterRegistry.get(smartContractMethod+"_sucess");
         failCounter = CounterRegistry.get(smartContractMethod+"_fail");
        return new Counter []{sucessCounter, failCounter};
    }

    public Histogram getHistogram (String smartContractMethod) {

        Histogram histogram = null;
        if(HistogramRegistry.get(smartContractMethod+"_latency") == null)
        {
            histogram = Histogram.build().name(smartContractMethod+"_latency").help("Transaction Latency").register();
            HistogramRegistry.put(smartContractMethod+"_latency", histogram);
        }
        histogram = HistogramRegistry.get(smartContractMethod+"_latency");
        return histogram;
    }

    
    
}
