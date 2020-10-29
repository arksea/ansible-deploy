package net.arksea.ansible.deploy.api;

/**
 * Create by xiaohaixing on 2020/9/10
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(String message, Throwable ex) {
        super(message, ex);
    }
}
