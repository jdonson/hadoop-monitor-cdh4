package com.wandoujia.hadoop.monitor.hbase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.HServerLoad;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.ipc.HMasterInterface;

import com.wandoujia.common.utils.StringUtils;
import com.wandoujia.common.utils.ThreadUtils;
import com.wandoujia.hadoop.monitor.Monitor;
import com.wandoujia.hadoop.monitor.comms.Constants;
import com.wandoujia.hadoop.monitor.comms.MonitorConfig;
import com.wandoujia.hadoop.monitor.jmx.RegionServerJMX;
import com.wandoujia.hbase.HBaseClient;

public class HBaseMonitor extends Monitor implements Runnable {
    private final Log logger = LogFactory.getLog(HBaseMonitor.class);

    private HBaseClient hbaseClient;

    public volatile boolean stopped = false;

    public HBaseMonitor() throws FileNotFoundException, IOException {
        hbaseClient = new HBaseClient();
    }

    private boolean filter(Collection<ServerName> deadServersName) {
        if (deadServersName == null || deadServersName.size() < 1) {
            return false;
        }
        String value = MonitorConfig.get(Constants.MONITOR_HBASE_WHITELIST);
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        logger.info("monitor hbase whitelist: " + value);
        String[] whiteList = value.split(",");
        List<String> badList = new ArrayList<String>();
        for (ServerName serverName: deadServersName) {
            boolean flag = false;
            for (String white: whiteList) {
                if (serverName.getServerName().contains(white)) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                badList.add(serverName.getServerName());
            }
        }
        if (badList != null && badList.size() > 0) {
            return false;
        }
        return true;
    }

    private void deadRegionServers(Collection<ServerName> deadServersName) {
        if (deadServersName == null || deadServersName.size() < 1) {
            return;
        }
        if (filter(deadServersName) == false) {
            sendMail(Constants.MSG_DEAD_REGIONSERVERS, " dead region servers: "
                    + deadServersName);
        }
    }

    private void masterRunning(boolean isRunning) {
        if (isRunning == false) {
            sendMail(Constants.MSG_MASTER_NOT_RUNNING,
                    " hbase master is not running!");
        }
    }

    private void liveRegionServerHealth(Map<String, HServerLoad> hServersLoad) {
        for (Map.Entry<String, HServerLoad> entry: hServersLoad.entrySet()) {
            String regionServer = entry.getKey();
            HServerLoad server = entry.getValue();
            int memStoreSizeMB = server.getMemStoreSizeInMB();
            int regions = server.getNumberOfRegions();
            int requests = server.getNumberOfRequests();
            int storefiles = server.getStorefiles();
            int storefileIndexSizeMB = server.getStorefileIndexSizeInMB();
            int storefilesSizeMB = server.getStorefileSizeInMB();
            int totalRequests = server.getTotalNumberOfRequests();
            int usedHeapMB = server.getUsedHeapMB();
            String msg = String
                    .format("[RegionServer] %s: memstore size(MB): %d, regions: %d, requests: %d, storefiles: %d, storefileIndexSizeMB: %d, storefilesSizeMB: %d, totalRequests: %d, usedHeapMB: %d",
                            regionServer, memStoreSizeMB, regions, requests,
                            storefiles, storefileIndexSizeMB, storefilesSizeMB,
                            totalRequests, usedHeapMB);
            logger.info(msg);
            try {
                Map<String, String> kvs = RegionServerJMX
                        .getJMXValues(regionServer);
                for (Map.Entry<String, String> kv: kvs.entrySet()) {
                    logger.info(String.format("key: %s, value: %s",
                            kv.getKey(), kv.getValue()));
                }
                // sink event to muce 2.0 dataserver
                kvs.put(Constants.FIELD_KEY_REGIONSERVER, regionServer);
                sink.sink(Constants.EVENT_HBASE_REGIONSERVER_METRICS, kvs);
            } catch (IOException e) {
                logger.error("", e);
            } catch (InterruptedException e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public void run() {
        logger.info("hbase monitor thread startup...");
        while (true) {
            if (stopped) {
                break;
            }
            try {
                HBaseAdmin hbaseAdmin = hbaseClient.getAdmin();
                HMasterInterface hbaseMaster = hbaseAdmin.getMaster();
                masterRunning(hbaseMaster.isMasterRunning());
                ClusterStatus status = hbaseAdmin.getClusterStatus();
                int deadServers = status.getDeadServers();
                Collection<ServerName> deadServersName = status
                        .getDeadServerNames();
                int regionsCount = status.getRegionsCount();
                int requestsCount = status.getRequestsCount();
                double avgLoad = status.getAverageLoad();
                Collection<ServerName> servers = status.getServers();
                Map<String, HServerLoad> hServersLoad = new HashMap<String, HServerLoad>();
                for (ServerName serverName: servers) {
                    hServersLoad.put(serverName.getHostname(),
                            status.getLoad(serverName));
                }
                logger.info("dead servers: " + deadServers);
                logger.info("regions count: " + regionsCount);
                logger.info("average load: " + avgLoad);
                logger.info("requests count: " + requestsCount);
                logger.info("servers: " + servers);
                logger.info("server info: " + servers);
                deadRegionServers(deadServersName);
                liveRegionServerHealth(hServersLoad);
            } catch (IOException e) {
                logger.error("Monitor Thread IOException", e);
                String msg = e.getMessage();
                if (msg == null) {
                    msg = "Monitor Thread IOException";
                }
                sendMail(getName() + " Monitor Thread IOException", msg);
            } catch (Exception e) {
                logger.error("Monitor Thread Exception", e);
                String msg = e.getMessage();
                if (msg == null) {
                    msg = "Monitor Thread Exception";
                }
                sendMail(getName() + " Monitor Thread Exception", msg);
            }
            ThreadUtils.sleep(Long.parseLong(MonitorConfig
                    .get(Constants.KEY_MONITOR_INTERVAL)));
        }

        logger.info("hbase monitor thread exit...");
    }

    @Override
    public String getName() {
        return "HBase-CDH";
    }
}
