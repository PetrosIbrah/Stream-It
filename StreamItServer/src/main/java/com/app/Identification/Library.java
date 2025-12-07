package com.app.Identification;

import jakarta.persistence.*;

@Entity
@Table(name = "library")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medianame", nullable = false)
    private String medianame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Accounts user;

    public Library(String medianame, Accounts user) {
        this.medianame = medianame;
        this.user = user;
    }

    @SuppressWarnings("unused")
    public Library() {}

    @SuppressWarnings("unused")
    public String getMedianame() { return medianame; }
    @SuppressWarnings("unused")
    public void setMedianame(String medianame) { this.medianame = medianame; }

    @SuppressWarnings("unused")
    public Accounts getUser() { return user; }
    @SuppressWarnings("unused")
    public void setUser(Accounts user) { this.user = user; }

    @SuppressWarnings("unused")
    public Long getId() { return id; }
    @SuppressWarnings("unused")
    public void setId(Long id) { this.id = id; }
}