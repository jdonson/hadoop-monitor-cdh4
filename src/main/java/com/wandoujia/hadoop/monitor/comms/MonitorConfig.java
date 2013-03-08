package com.wandoujia.hadoop.monitor.comms;

import org.apache.hadoop.conf.Configuration;

public class MonitorConfig {

    public static String get(String name) {
        Configuration conf = new Configuration();
        conf.addResource("hadoop-monitor.xml");
        return conf.get(name);
    }
}
