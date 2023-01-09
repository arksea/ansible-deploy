package net.arksea.ansible.deploy.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Create by xiaohaixing on 2020/9/10
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 7826483247237876901L;
    public static final Logger logger = LogManager.getLogger(ServiceException.class);
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(String message, Throwable ex) {
        super(message, ex);
    }
}
