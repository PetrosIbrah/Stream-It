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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_library",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "library_id")
    )
    private Set<Library> library = new HashSet<>();

    public Accounts( String username, String password) {
        this.username = username;
        this.password = password;
    }

    @SuppressWarnings("unused")
    public Accounts() {}

    @SuppressWarnings("unused")
    public Accounts(Long userid, String username, String password) {
        this.userid = userid;
        this.username = username;
        this.password = password;
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
    public String getPassword() {return password;}
    @SuppressWarnings("unused")
    public void setPassword(String password) {this.password = password;}

    @SuppressWarnings("unused")
    public void addLibraryItem(Library item) {
        library.add(item);
    }
    @SuppressWarnings("unused")
    public void removeLibraryItem(Library item) {
        library.remove(item);
    }
}