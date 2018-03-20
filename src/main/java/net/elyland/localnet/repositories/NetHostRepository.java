package net.elyland.localnet.repositories;

import net.elyland.localnet.domains.NetHost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetHostRepository extends JpaRepository<NetHost, Integer> {
    NetHost findByHostname(String hostname);
    NetHost findByIpAddress(String ipAddress);
    NetHost findByMacAddress(String macAddress);
}
