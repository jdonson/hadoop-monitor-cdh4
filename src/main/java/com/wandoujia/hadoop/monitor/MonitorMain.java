package com.wandoujia.hadoop.monitor;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wandoujia.hadoop.monitor.hbase.HBaseMonitor;
import com.wandoujia.hadoop.monitor.hdfs.HDFSMonitor;

public class MonitorMain {
    public static final Log logger = LogFactory.getLog(MonitorMain.class);

    private HBaseMonitor hbaseMonitor;

    private HDFSMonitor hdfsMonitor;

    public MonitorMain() throws IOException {
        System.setProperty("HADOOP_USER_NAME", "work");
        hbaseMonitor = new HBaseMonitor();
        hdfsMonitor = new HDFSMonitor();
    }

    public void startup() {
        Thread threadHBaseMonitor = new Thread(hbaseMonitor);
        Thread threadHDFSMonitor = new Thread(hdfsMonitor);
        threadHBaseMonitor.start();
        threadHDFSMonitor.start();
    }

    public static void main(String[] args) throws IOException {
        logger.info("startup hadoop/hbase monitor...");
        MonitorMain monitor = new MonitorMain();
        monitor.startup();
    }

}
