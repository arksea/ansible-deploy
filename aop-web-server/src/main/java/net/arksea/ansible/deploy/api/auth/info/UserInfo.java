package net.arksea.ansible.deploy.api.auth.info;


import net.arksea.ansible.deploy.api.auth.entity.Role;

import java.io.Serializable;
import java.util.Set;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 652109782037629138L;

    public final long id;
    public final String name;
    public final String email;
    public final int locked;
    public final String lastLogin;
    public final String registerDate;
    public final Set<Role> roles;


    public UserInfo(long id, String name, String email, int locked, String lastLogin, String registerDate, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.locked = locked;
        this.lastLogin = lastLogin;
        this.registerDate = registerDate;
        this.roles = roles;
    }
}
