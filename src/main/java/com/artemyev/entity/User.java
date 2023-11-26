package com.artemyev.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
@ToString(exclude = {"company", "profile", "usersChats"})
@Builder
@Entity
@Table(name = "users", schema = "public")
public class User implements Comparable<User>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(generator = "user_seq", strategy = GenerationType.SEQUENCE)
//    @SequenceGenerator(name = "user_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Embedded
    @AttributeOverride(name = "firstname", column = @Column(name = "firstname"))
    @AttributeOverride(name = "lastname", column = @Column(name = "lastname"))
    @AttributeOverride(name = "birthday", column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER /* cascade = {CascadeType.ALL} */)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false
    )
    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UsersChat> usersChats = new ArrayList<>();

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

//    @Builder.Default
//    @ManyToMany
//    @JoinTable(
//            name = "users_chat",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "chat_id")
//    )
//    private List<Chat> chats = new ArrayList<>();
//
//    public void addChat (Chat chat) {
//        chats.add(chat);
//        chat.getUsers().add(this);
//    }

//    @Column(name = "info")
//    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
//    private String info;
}
