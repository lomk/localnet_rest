package net.elyland.localnet.services;

/**
 * Created by Igor on 29-Jun-16.
 */

public interface SecurityService {
    String findLoggedInUsername();

    void autologin(String username, String password);
}
