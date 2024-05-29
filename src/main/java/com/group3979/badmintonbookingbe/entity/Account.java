package com.group3979.badmintonbookingbe.entity;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Gender;
import com.group3979.badmintonbookingbe.eNum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account implements UserDetails{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true )
    private String phone;
    @Column(unique = true )
    private String email;
    private String name;
    @Enumerated(EnumType.STRING)
    Gender gender;
    @Enumerated(EnumType.STRING)
    Role role;
    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToMany(mappedBy = "account")
    List<Club> clubs;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
