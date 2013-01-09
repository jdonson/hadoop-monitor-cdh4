package com.wandoujia.hadoop.monitor;

public class Constants {
    public static final String[] receivers = {
        "fengzanfeng@wandoujia.com"
    };

    public static final long interval = 60000;

    // HDFS Monitor
    public static final String MSG_DEAD_DATANODES = "[HDFS Monitor] [DataNode] Found Dead DataNode";

    public static final String MONITOR_HDFS_WHITELIST = "monitor.hdfs.whitelist";

    // HBase Monitor

    public static final String MONITOR_HBASE_WHITELIST = "monitor.hbase.whitelist";

    public static final String MSG_DEAD_REGIONSERVERS = "[HBase Monitor] [RegionServer] Found Dead Region Servers";

    public static final String MSG_LOAD_REQUESTS = "[HBase Monitor] [RegionServer] Too Many Requests";

    public static final String MSG_LOAD_REGIONS = "[HBase Monitor] [RegionServer] Too Many Regions";

}