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

    public static String[] metrics = {
        "ReceivedBytes", "SentBytes", "RpcQueueTimeNumOps",
        "RpcQueueTimeAvgTime", "RpcProcessingTimeNumOps",
        "RpcProcessingTimeAvgTime", "NumOpenConnections", "CallQueueLength",
        "CapacityTotalGB", "CapacityUsedGB", "CapacityRemainingGB",
        "TotalLoad", "BlocksTotal", "FilesTotal", "PendingReplicationBlocks",
        "UnderReplicatedBlocks", "CorruptBlocks", "FsyncNumOps", "FsyncAvgTime"
    };

    public static Map<String, String> getJMXValues(String host)
            throws IOException {
        Map<String, String> values = new HashMap<String, String>();
        String url = String.format("http://%s:%d/jmx", host, NAMENODE_PORT);
        String jmxJson = JMXUtils.fetchJMX(url);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jmxJson);
        for (String metric: metrics) {
            List<JsonNode> nodes = root.findValues(metric);
            if (nodes == null || nodes.size() != 1) {
                logger.warn("get namenode metrics failed: " + metric);
                continue;
            }
            String value = nodes.get(0).asText();
            values.put(metric, value);
        }
        return values;
    }
}
