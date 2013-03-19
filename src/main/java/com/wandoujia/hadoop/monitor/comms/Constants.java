package com.wandoujia.hadoop.monitor.comms;

public class Constants {
    public static final long interval = 60000;

    public static final String KEY_CLUSTER_NAME = "cluster.name";

    public static final String KEY_MONITOR_INTERVAL = "monitor.interval";

    public static final String KEY_MAIL_RECEIVERS = "mail.receivers";

    // HDFS Monitor
    public static final String MSG_DEAD_DATANODES = "[HDFS CDH Monitor] [DataNode] Found Dead DataNode";

    public static final String MONITOR_HDFS_WHITELIST = "monitor.hdfs.whitelist";

    // HBase Monitor
    public static final String MONITOR_HBASE_WHITELIST = "monitor.hbase.whitelist";

    public static final String MSG_DEAD_REGIONSERVERS = "[HBase CDH Monitor] [RegionServer] Found Dead Region Servers";

    public static final String MSG_LOAD_REQUESTS = "[HBase CDH Monitor] [RegionServer] Too Many Requests";

    public static final String MSG_LOAD_REGIONS = "[HBase CDH Monitor] [RegionServer] Too Many Regions";

    public static final String MSG_MASTER_NOT_RUNNING = "[HBase CDH Monitor] [HMaster] hbase master not running";

    // data sink
    public static final String FIELD_KEY_CLUSTER = "cluster";

    public static final String FIELD_KEY_NAMENODE = "namenode";

    public static final String FIELD_KEY_DATANODE = "datanode";

    public static final String FIELD_KEY_REGIONSERVER = "regionserver";

    public static final String KEY_DATASINK_DATASERVER = "datasink.server";

    public static final String KEY_DATASINK_PROFILE = "datasink.profile";

    // public static final String DATASINK_DATASERVER = "l.wandoujia.com";

    public static final String EVENT_HADOOP_NAMENODE_METRICS = "hadoop.namenode.metrics";

    public static final String EVENT_HADOOP_DATANODE_METRICS = "hadoop.datanode.metrics";

    public static final String EVENT_HBASE_REGIONSERVER_METRICS = "hbase.regionserver.metrics";

}
