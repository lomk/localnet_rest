package net.elyland.localnet.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Igor on 29-Jun-16.
 */
@Entity
@Table(schema = "localnet", name = "role")
@Getter
@Setter
public class Role implements Serializable {
    private static final long serialVersionUID = -1200119078147252005L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;

//    @OneToMany(mappedBy = "role")
//    private Set<User> users;

}
