package net.elyland.localnet.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Created by Igor on 17-Jun-16.
 */
@Entity
@Table(schema = "localnet", name = "user")
@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = -1000119078147252012L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Transient
    private String passwordConfirm;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


}
