package net.elyland.localnet.domains;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(schema = "localnet", name = "place")
@Getter
@Setter
public class Place {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "port_number")
    private Integer portNumber;

    @Column(name = "name")
    private String name;
}
