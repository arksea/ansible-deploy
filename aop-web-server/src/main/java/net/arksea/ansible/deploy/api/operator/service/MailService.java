package net.arksea.ansible.deploy.api.operator.service;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

@Component
public class MailService {
    private Logger logger = LogManager.getLogger(MailService.class);
    @Value("${job.notify.mail.host}")
    private String host = null;
    @Value("${job.notify.mail.port}")
    private int port;
    @Value("${job.notify.mail.username}")
    private String username = null;
    @Value("${job.notify.mail.password}")
    private String password = null;
    @Value("${job.notify.mail.from}")
    private String from = null;
    @Value("${job.notify.mail.timeout:30000}")
    private int timeout;
    @Value("${job.notify.mail.ssl:true}")
    private boolean ssl;

    private Authenticator auth;
    private Properties props;

    @PostConstruct
    void init() throws GeneralSecurityException {
        this.auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        this.props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", Integer.toString(port));
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.timeout", Integer.toString(timeout));
        if (ssl) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", sf);
        }
    }

    public void send(String recipients, String subject, String html) {
        try {
            Session session = Session.getDefaultInstance(props, auth);
            if (logger.isDebugEnabled()) {
                session.setDebug(true);
            }
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            String[] strs = StringUtils.split(recipients, ";, \n");
            for (String to: strs) {
                message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            }
            message.setSubject(subject);
            message.setContent(html, "text/html;charset=utf-8");
            Transport.send(message);
        } catch (Exception ex) {
            logger.warn("Send email failed, recipients: {}, subject: {}",recipients, subject, ex);
        }
    }
}
