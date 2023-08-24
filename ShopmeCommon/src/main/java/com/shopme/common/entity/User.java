package com.shopme.common.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 128, nullable = false, unique = true)
    @NotNull
    @Size(
            min = 3,
            max = 128,
            message = "Email not valid"
    )
    private String email;

    @Column(length = 64, nullable = false)
    @NotNull
    @Size(
            min = 6,
            max = 64,
            message = "password not valid"
    )
    private String password;

    @Column(name = "first_name", length = 45, nullable = false)
    @NotNull
    @Size(
            min = 3,
            max = 45,
            message = "firstName not valid"
    )
    private String firstName;

    @Column(name = "last_name", length = 45, nullable = false)
    @NotNull
    @Size(
            max = 45,
            message = "lastName not valid"
    )
    private String lastName;

    @Column(length = 64)
    private String photos;
    private boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @NotNull
    @NotEmpty(message = "roles is mandatory")
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRoles(Role role) {
        this.roles.add(role);
    }

    @Transient
    public String getPhotosImagePath() {
        if (id ==0 || photos == null) return "/images/default-user.png";

        return "/user-photos/" + this.id + "/" + this.photos;
    }
}
