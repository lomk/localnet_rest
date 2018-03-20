package net.elyland.localnet.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * Created by imaterynko on 17.01.17.
 */
@Entity
@Table(schema = "localnet", name = "server")
@Getter
@Setter
public class NetHost implements Serializable {
    private static final long serialVersionUID = -1000129576177292006L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hostname",  length = 100)
    private String hostname;

    @Column(name = "ip_address", length = 16)
    private String ipAddress;

    @Column(name = "mac_address", length = 30)
    private String macAddress;

    @Column(name = "os")
    private String os;

    @Column(name = "is_up")
    private Boolean isUp;

    @OneToMany(mappedBy = "host", targetEntity = Port.class)
    @OrderBy("port_number")
    public Set<Port> ports;
}
