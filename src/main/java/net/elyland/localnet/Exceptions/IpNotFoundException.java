package net.elyland.localnet.Exceptions;

/**
 * Created by imaterynko on 24.07.17.
 */
public class IpNotFoundException extends Exception {
    public IpNotFoundException(String msg) {
        super(msg);
    }

    public IpNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
