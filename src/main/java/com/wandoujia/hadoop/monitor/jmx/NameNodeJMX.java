package com.wandoujia.hadoop.monitor.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class NameNodeJMX {
    public static final Log logger = LogFactory.getLog(NameNodeJMX.class);

    public static final int NAMENODE_PORT = 50070;

    public static final String JMX_URL = "http://%s:%d/jmx";

    public static String[] metrics = {
        "ReceivedBytes", "SentBytes", "RpcQueueTimeNumOps",
        "RpcQueueTimeAvgTime", "RpcProcessingTimeNumOps",
        "RpcProcessingTimeAvgTime", "NumOpenConnections", "CallQueueLength",
        "CapacityTotalGB", "CapacityUsedGB", "CapacityRemainingGB",
        "TotalLoad", "BlocksTotal", "FilesTotal", "PendingReplicationBlocks",
        "UnderReplicatedBlocks", "CorruptBlocks", "FsyncNumOps",
        "FsyncAvgTime", "CreateFileOps", "FilesCreated", "FilesRenamed",
        "GetListingOps", "DeleteFileOps", "FilesDeleted", "AddBlockOps",
        "NumLiveDataNodes", "NumDeadDataNodes"
    };

    public static Map<String, String> getMetrics(String host)
            throws IOException {
        Map<String, String> values = new HashMap<String, String>();
        String url = String.format(JMX_URL, host, NAMENODE_PORT);
        logger.info("fetching jmx from url: " + url);
        String jmxJson = JMXUtils.fetchJMX(url);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jmxJson);
        for (String metric: metrics) {
            List<JsonNode> nodes = root.findValues(metric);
            if (nodes == null || nodes.size() != 1) {
                continue;
            }
            String value = nodes.get(0).asText();
            values.put(metric, value);
        }
        return values;
    }
}
