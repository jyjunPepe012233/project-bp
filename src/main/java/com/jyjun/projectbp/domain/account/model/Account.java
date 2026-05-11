package com.jyjun.projectbp.domain.account.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String encodedPassword;

    protected Account() {
    }

    public Account(String name, String encodedPassword) {
        this.name = name;
        this.encodedPassword = encodedPassword;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
}

// 현재 계정 정보 수정은 본인의 계정만 가능하도록 구현되어 있음
// 근데 루트 계정이나 ADMIN 계정이 다른 하위 게정을 수정할 수 있도록 해야함
//