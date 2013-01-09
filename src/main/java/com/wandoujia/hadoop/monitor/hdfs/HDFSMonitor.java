package com.wandoujia.hadoop.monitor.hdfs;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem.Statistics;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.HdfsConstants.DatanodeReportType;

import com.wandoujia.common.utils.StringUtils;
import com.wandoujia.common.utils.ThreadUtils;
import com.wandoujia.hadoop.hdfs.HDFSClient;
import com.wandoujia.hadoop.monitor.Constants;
import com.wandoujia.hadoop.monitor.Monitor;
import com.wandoujia.hadoop.monitor.MonitorConfig;

public class HDFSMonitor extends Monitor implements Runnable {
    private final Log logger = LogFactory.getLog(HDFSMonitor.class);

    public volatile boolean stopped = false;

    private HDFSClient hdfsClient;

    private DistributedFileSystem dfs;

    public HDFSMonitor() throws IOException {
        hdfsClient = new HDFSClient();
        dfs = (DistributedFileSystem) hdfsClient.getFileSystem();
    }

    private boolean filter(DatanodeInfo[] deadDataNodes) {
        if (deadDataNodes == null) {
            return false;
        }
        String value = MonitorConfig.get(Constants.MONITOR_HDFS_WHITELIST);
        logger.info("monitor hdfs whitelist: " + value);
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        String[] whiteList = value.split(",");
        List<String> badList = new ArrayList<String>();
        for (DatanodeInfo deadDataNode: deadDataNodes) {
            boolean flag = false;
            for (String white: whiteList) {
                if (deadDataNode.getName().contains(white)) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                badList.add(deadDataNode.getName());
            }
        }
        if (badList != null && badList.size() > 0) {
            return false;
        }
        return true;
    }

    private void corruptBlocks(long corruptBlocksCount) {
        if (corruptBlocksCount > 0) {
            String msg = "found corrupt blocks: " + corruptBlocksCount;
            logger.warn(msg);
            sendMail(getName() + " Corrupt Blocks", msg);
        }
    }

    private void deadDataNodes(DatanodeInfo[] deadDataNodes) {
        if (deadDataNodes == null || deadDataNodes.length < 1) {
            return;
        }
        if (filter(deadDataNodes) == false) {
            sendMail(Constants.MSG_DEAD_DATANODES,
                    Arrays.toString(deadDataNodes));
        }
    }

    @Override
    public void run() {
        logger.info("hdfs monitor thread startup...");
        while (true) {
            if (stopped) {
                break;
            }
            try {
                URI uri = dfs.getUri();
                String serviceName = dfs.getCanonicalServiceName();
                long corruptBlocksCount = dfs.getCorruptBlocksCount();
                long missingBlocksCount = dfs.getMissingBlocksCount();
                long underReplicateBlocksCount = dfs
                        .getUnderReplicatedBlocksCount();
                DatanodeInfo[] deadDataNodes = dfs.getClient().datanodeReport(
                        DatanodeReportType.DEAD);
                List<Statistics> statistics = DistributedFileSystem
                        .getAllStatistics();
                long used = dfs.getUsed();
                logger.info("hdfs uri: " + uri);
                logger.info("hdfs service name: " + serviceName);
                logger.info("hdfs corrupt blocks count: " + corruptBlocksCount);
                logger.info("hdfs missing blocks count: " + missingBlocksCount);
                logger.info("hdfs under replicate blocks count: "
                        + underReplicateBlocksCount);
                logger.info("hdfs statistics: " + statistics);
                logger.info("hdfs used: " + used);
                corruptBlocks(corruptBlocksCount);
                deadDataNodes(deadDataNodes);
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
        logger.info("hdfs monitor thread exit...");
    }

    @Override
    public String getName() {
        return "HDFS";
    }

}
