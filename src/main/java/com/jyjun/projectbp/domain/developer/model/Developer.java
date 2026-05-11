package com.jyjun.projectbp.domain.developer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_developer")
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, updatable = false, unique = true)
    private Long rootAccountId;

    protected Developer() {
    }

    public Developer(String name, Long rootAccountId) {
        this.name = name;
        this.rootAccountId = rootAccountId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getRootAccountId() {
        return rootAccountId;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
