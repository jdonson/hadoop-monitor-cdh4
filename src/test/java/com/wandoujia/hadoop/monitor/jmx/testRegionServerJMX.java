package com.wandoujia.hadoop.monitor.jmx;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

public class testRegionServerJMX extends TestCase {
    public void testGetMetrics() throws IOException {
        Map<String, String> kvs = RegionServerJMX
                .getJMXValues("hadoop-anode00");
        for (Map.Entry<String, String> entry: kvs.entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: "
                    + entry.getValue());
        }

        kvs = NameNodeJMX.getMetrics("hadoop-anode00");
        for (Map.Entry<String, String> entry: kvs.entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: "
                    + entry.getValue());
        }
    }
}
