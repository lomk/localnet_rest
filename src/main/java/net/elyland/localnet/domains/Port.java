package net.elyland.localnet.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(schema = "localnet", name = "role")
@Getter
@Setter
public class Port {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "port_number")
    private Integer portNumber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "host_id")
    private NetHost host;
}
