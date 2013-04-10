package com.wandoujia.hadoop.monitor.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class RegionServerJMX {
    public static final Log logger = LogFactory.getLog(RegionServerJMX.class);

    public static final int REGIONSERVER_PORT = 60030;

    public static String[] metrics = {
        "SentBytes", "ReceivedBytes", "getNumOps", "getAvgTime", "putNumOps",
        "putAvgTime", "openScannerNumOps", "openScannerAvgTime",
        "deleteNumOps", "deleteAvgTime", "RpcProcessingTimeNumOps",
        "RpcProcessingTimeAvgTime", "RpcQueueTimeNumOps",
        "RpcQueueTimeAvgTime", "memstoreSizeMB", "blockCacheFree",
        "blockCacheHitRatio", "splitRegionNumOps", "splitRegionAvgTime",
        "compactionQueueSize", "compactionTimeNumOps", "compactionTimeAvgTime",
        "compactionSizeNumOps", "compactionSizeAvgTime", "regions",
        "storefiles", "stores", "requests", "blockCacheCount",
        "blockCacheEvictedCount", "blockCacheHitCount", "blockCacheMissCount",
        "fsReadLatencyAvgTime", "fsReadLatencyNumOps", "fsWriteLatencyAvgTime",
        "fsWriteLatencyNumOps", "NumOpenConnections", "callQueueLen"
    };

    public static Map<String, String> getJMXValues(String host)
            throws IOException {
        Map<String, String> values = new HashMap<String, String>();
        String url = String.format("http://%s:%d/jmx", host, REGIONSERVER_PORT);
        String jmxJson = JMXUtils.fetchJMX(url);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jmxJson);
        for (String metric: metrics) {
            List<JsonNode> nodes = root.findValues(metric);
            if (nodes == null || nodes.size() != 1) {
                logger.warn("get regionserver metrics failed: " + metric);
                continue;
            }
            String value = nodes.get(0).asText();
            values.put(metric, value);
        }
        return values;
    }
}
