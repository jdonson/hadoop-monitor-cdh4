package com.wandoujia.hadoop.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.EmailException;

import com.wandoujia.common.utils.MailUtils;

public abstract class Monitor {
    public final static Log logger = LogFactory.getLog(Monitor.class);

    public abstract String getName();

    public static void sendMail(String subject, String msg) {
        try {
            logger.info("send mail, subject: " + subject + ", msg: " + msg);
            MailUtils.sendMail(subject, null, msg, Constants.receivers);
        } catch (EmailException e) {
            logger.error("", e);
        }
    }
}
