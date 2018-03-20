package net.elyland.localnet.services;


import net.elyland.localnet.domains.User;

/**
 * Created by Igor on 17-Jun-16.
 */
public interface UserService {
    Iterable<User> listAllUsers();
    User getUserById(Integer id);
    void deleteUser(Integer id);
    void save(User user);
    public User findByUsername(String username);
}
