package net.arksea.ansible.deploy.api.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final boolean locked;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Collection<String> roles;

    public UserDetailsImpl(String username, String password, boolean accountLocked,
                           Collection<? extends GrantedAuthority> authorities, Collection<String> roles) {
        this.username = username;
        this.password = password;
        this.locked = accountLocked;
        this.authorities = authorities;
        this.roles = roles;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !locked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !locked;
    }

    @Override
    public boolean isEnabled() {
        return !locked;
    }
}
