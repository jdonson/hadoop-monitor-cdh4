package com.wandoujia.hadoop.monitor;

import java.io.IOException;

import com.wandoujia.hadoop.monitor.hbase.HBaseMonitor;
import com.wandoujia.hadoop.monitor.hdfs.HDFSMonitor;

public class MonitorMain {
    private HBaseMonitor hbaseMonitor;

    private HDFSMonitor hdfsMonitor;

    public MonitorMain() throws IOException {
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
        MonitorMain monitor = new MonitorMain();
        monitor.startup();
    }

}
