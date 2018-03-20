package net.elyland.localnet.Exceptions;

/**
 * Created by imaterynko on 07.08.17.
 */
public class UserIpExistException extends Exception {
    public UserIpExistException(String msg) {
        super(msg);
    }

    public UserIpExistException(String msg, Throwable t) {
        super(msg, t);
    }
}
