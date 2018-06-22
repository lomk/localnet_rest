package net.elyland.localnet.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;


/**
 * Created by imaterynko on 17.01.17.
 */
@Entity
@Table(schema = "localnet", name = "host")
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

    @Column(name = "mac_address", length = 30, unique = true)
    private String macAddress;

    @Column(name = "os")
    private String os;

    @Column(name = "custom_name")
    private String customName;

    @Column(name = "is_up")
    private Boolean isUp;

    @Temporal(TemporalType.TIMESTAMP)
    private Date scanTime;

    @OneToMany(mappedBy = "host", targetEntity = Port.class)
    @OrderBy("port_number")
    public Set<Port> ports;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
}
