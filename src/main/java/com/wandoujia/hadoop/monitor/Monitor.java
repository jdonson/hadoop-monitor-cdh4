package com.wandoujia.hadoop.monitor;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.EmailException;

import com.wandoujia.common.utils.MailUtils;
import com.wandoujia.common.utils.StringUtils;
import com.wandoujia.hadoop.monitor.comms.Constants;
import com.wandoujia.hadoop.monitor.comms.MonitorConfig;
import com.wandoujia.muce.client.sink.DataSink;
import com.wandoujia.muce.client.sink.HttpDataSink;

public abstract class Monitor {
    public final static Log logger = LogFactory.getLog(Monitor.class);

    protected DataSink sink = new HttpDataSink(
            MonitorConfig.get(Constants.KEY_DATASINK_PROFILE),
            MonitorConfig.get(Constants.KEY_DATASINK_DATASERVER));

    public Monitor() {
        // add cluster name to header
        sink.addHeader(Constants.FIELD_KEY_CLUSTER,
                MonitorConfig.get(Constants.KEY_CLUSTER_NAME));
    }

    public abstract String getName();

    public static void sendMail(String subject, String msg) {
        try {
            String value = MonitorConfig.get(Constants.KEY_MAIL_RECEIVERS);
            if (StringUtils.isEmpty(value)) {
                return;
            }
            String[] receivers = null;
            if (value.contains(",")) {
                receivers = value.split(",");
            } else if (value.contains(" ")) {
                receivers = value.split(" ");
            } else if (value.contains(" ")) {
                receivers = value.split(";");
            } else {
                receivers = new String[] {
                    value
                };
            }
            logger.info("send mail, subject: " + subject + ", msg: " + msg);
            MailUtils.sendMail(subject, null, msg, receivers);
        } catch (EmailException e) {
            logger.error("", e);
        }
    }

    public synchronized void close() throws InterruptedException, IOException {
        if (sink != null) {
            sink.close();
        }
    }
}
