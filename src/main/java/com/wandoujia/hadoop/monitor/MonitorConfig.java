package com.wandoujia.hadoop.monitor;

import org.apache.hadoop.conf.Configuration;

public class MonitorConfig {

    public static String get(String name) {
        Configuration conf = new Configuration();
        conf.addResource("monitor-site.xml");
        return conf.get(name);
    }
}
