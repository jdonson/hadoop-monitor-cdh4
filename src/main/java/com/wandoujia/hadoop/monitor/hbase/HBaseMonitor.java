package com.wandoujia.hadoop.monitor.hbase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import com.wandoujia.common.utils.StringUtils;
import com.wandoujia.common.utils.ThreadUtils;
import com.wandoujia.hadoop.monitor.Constants;
import com.wandoujia.hadoop.monitor.Monitor;
import com.wandoujia.hadoop.monitor.MonitorConfig;
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

    @Override
    public void run() {
        logger.info("hbase monitor thread startup...");
        while (true) {
            if (stopped) {
                break;
            }
            try {
                HBaseAdmin hbaseAdmin = hbaseClient.getAdmin();
                ClusterStatus status = hbaseAdmin.getClusterStatus();
                int deadServers = status.getDeadServers();
                Collection<ServerName> deadServersName = status
                        .getDeadServerNames();
                int regionCount = status.getRegionsCount();
                int requestsCount = status.getRequestsCount();
                Collection<ServerName> servers = status.getServers();
                logger.info("dead servers: " + deadServers);
                logger.info("region count: " + regionCount);
                logger.info("requests count: " + requestsCount);
                logger.info("servers: " + servers);
                logger.info("server info: " + servers);
                deadRegionServers(deadServersName);
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
            ThreadUtils.sleep(Constants.interval);
        }

        logger.info("hbase monitor thread exit...");
    }

    @Override
    public String getName() {
        return "HBase";
    }
}
