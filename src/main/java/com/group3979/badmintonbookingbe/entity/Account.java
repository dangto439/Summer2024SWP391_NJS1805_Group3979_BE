package com.group3979.badmintonbookingbe.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group3979.badmintonbookingbe.eNum.AccountStatus;
import com.group3979.badmintonbookingbe.eNum.Gender;
import com.group3979.badmintonbookingbe.eNum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true )
    private String phone;
    @Column(unique = true )
    private String email;
    private String name;
    private Long supervisorID;
    private String avatar;
    @Enumerated(EnumType.STRING)
    Gender gender;
    @Enumerated(EnumType.STRING)
    Role role;
    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private LocalDate signupDate;

    @JsonIgnore
    @OneToOne(mappedBy = "account")
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "club_id")
    Club club;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    List<Club> clubs;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    List<Booking> bookings;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    List<Registration> registrations;

    @JsonIgnore
    @OneToMany(mappedBy = "firstPlayer")
    List<Game> matchesOfFirstPlayer;
    @JsonIgnore
    @OneToMany(mappedBy = "secondPlayer")
    List<Game> matchesOfSecondPlayer;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (this.role != null) {
            authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
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
