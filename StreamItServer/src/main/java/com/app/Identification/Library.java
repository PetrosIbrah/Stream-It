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

    public Library() {}

    public Library(String medianame, Accounts user) {
        this.medianame = medianame;
        this.user = user;
    }

    public String getMedianame() { return medianame; }
    public void setMedianame(String medianame) { this.medianame = medianame; }

    public Accounts getUser() { return user; }
    public void setUser(Accounts user) { this.user = user; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
