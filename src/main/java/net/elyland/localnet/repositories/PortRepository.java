package net.elyland.localnet.repositories;

import net.elyland.localnet.domains.Port;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortRepository extends JpaRepository<Port, Integer> {
}
