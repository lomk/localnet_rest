package net.elyland.localnet.repositories;


import net.elyland.localnet.domains.NetHost;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by imaterynko on 17.01.17.
 */
//@Repository

public interface ServerRepository extends JpaRepository<NetHost, Integer> {
    NetHost findByHostname(String hostname);
    NetHost findByIpAddress(String ipAddress);
}
