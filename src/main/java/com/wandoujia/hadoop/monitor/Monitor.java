package com.wandoujia.hadoop.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.EmailException;

import com.wandoujia.common.utils.MailUtils;
import com.wandoujia.common.utils.StringUtils;

public abstract class Monitor {
    public final static Log logger = LogFactory.getLog(Monitor.class);

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
}
