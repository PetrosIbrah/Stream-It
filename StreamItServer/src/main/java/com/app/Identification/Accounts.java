package com.app.Identification;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Accounts")
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid;

    @Column(name = "token")
    private String token;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_library",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "library_id")
    )
    private Set<Library> library = new HashSet<>();

    public Accounts( String username, String email) {
        this.username = username;
        this.email = email;
    }

    @SuppressWarnings("unused")
    public Accounts() {}

    @SuppressWarnings("unused")
    public Accounts(Long userid, String username, String email) {
        this.userid = userid;
        this.username = username;
        this.email = email;
    }

    @SuppressWarnings("unused")
    public Long getUserid() {return userid;}
    @SuppressWarnings("unused")
    public void setUserid(Long userid) {this.userid = userid;}

    @SuppressWarnings("unused")
    public String getUsername() {return username;}
    @SuppressWarnings("unused")
    public void setUsername(String username) {this.username = username;}

    @SuppressWarnings("unused")
    public void addLibraryItem(Library item) {
        library.add(item);
    }
    @SuppressWarnings("unused")
    public void removeLibraryItem(Library item) {
        library.remove(item);
    }
    @SuppressWarnings("unused")
    public String getEmail() {return email;}
    @SuppressWarnings("unused")
    public void setEmail(String email) {this.email = email;}

    public String getToken() {return token;}
    public void setToken(String token) {
        this.token = token;
    }
}