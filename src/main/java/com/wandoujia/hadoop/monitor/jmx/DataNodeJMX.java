package com.wandoujia.hadoop.monitor.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class DataNodeJMX {
    public static final Log logger = LogFactory.getLog(DataNodeJMX.class);

    public static final int DATANODE_PORT = 50075;

    public static final String JMX_URL = "http://%s:%d/jmx";

    public static String[] metrics = {
        "BytesWritten", "BytesRead", "BlocksWritten", "BlocksRead",
        "ReadsFromLocalClient", "ReadsFromRemoteClient",
        "WritesFromLocalClient", "WritesFromRemoteClient", "FsyncCount",
        "VolumeFailures", "RpcQueueTimeNumOps", "RpcQueueTimeAvgTime",
        "RpcProcessingTimeNumOps", "RpcProcessingTimeAvgTime",
        "NumOpenConnections", "CallQueueLength", "ReadBlockOpNumOps",
        "ReadBlockOpAvgTime", "WriteBlockOpNumOps", "WriteBlockOpAvgTime",
        "BlockReportsAvgTime"
    };

    public static Map<String, String> getMetrics(String host)
            throws IOException {
        Map<String, String> values = new HashMap<String, String>();
        String url = String.format(JMX_URL, host, DATANODE_PORT);
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
