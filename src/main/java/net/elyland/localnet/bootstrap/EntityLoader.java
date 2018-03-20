package net.elyland.localnet.bootstrap;

import net.elyland.localnet.domains.NetHost;
import net.elyland.localnet.domains.Role;
import net.elyland.localnet.domains.User;
import net.elyland.localnet.repositories.NetHostRepository;
import net.elyland.localnet.repositories.RoleRepository;
import net.elyland.localnet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;

@Component
public class EntityLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    NetHostRepository netHostRepository;



    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);


        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin123456");
        user.setRole(roleRepository.findByName("ADMIN"));
        userService.save(user);

//        NetHost netHost = new NetHost();
//        netHost.setMacAddress("er:er:er:er:er");
//        netHost.setIpAddress("192.168.0.1");
//        netHost.setHostname("Hostname");
//        netHostRepository.save(netHost);

    }
}

