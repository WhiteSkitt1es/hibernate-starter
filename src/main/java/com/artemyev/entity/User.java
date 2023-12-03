package com.artemyev.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;

import java.util.ArrayList;
import java.util.List;

//@NamedEntityGraph(name = "withCompanyAndChat",
//        attributeNodes = {
//                @NamedAttributeNode("company"),
//                @NamedAttributeNode("payments"),
//                @NamedAttributeNode(value = "usersChats", subgraph = "chats")
//        },
//        subgraphs = {
//                @NamedSubgraph(name = "chats", attributeNodes = @NamedAttributeNode("chat"))
//        }
//)
//@FetchProfile(name = "withCompanyAndPayments", fetchOverrides = {
//        @FetchProfile.FetchOverride(
//                entity = User.class, association = "company", mode = FetchMode.JOIN
//        ),
//        @FetchProfile.FetchOverride(
//                entity = User.class, association = "payments", mode = FetchMode.JOIN
//        )
//})
// To work with one entity by id (get(), load()) N + 1
//@NamedQuery(name = "findUserByName", query = "select u from User u " +
//                                             "join u.company c where u.personalInfo.firstname = :firstname and c.name = :name" +
//                                             " order by u.personalInfo.lastname")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
@ToString(exclude = {"company", /*"profile",*/ "usersChats", "payments"})
@Builder
@Entity
@Table(name = "users", schema = "public")
public class User implements Comparable<User>, BaseEntity<Long> {

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

    @ManyToOne(fetch = FetchType.LAZY /* cascade = {CascadeType.ALL} */)
    @JoinColumn(name = "company_id")
    private Company company;

//    @OneToOne(
//            mappedBy = "user",
//            cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY
//    )
//    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UsersChat> usersChats = new ArrayList<>();

    @Builder.Default
//    @BatchSize( size = 3)
//    1 + N -> 1 + 5 -> 1 + 5/3 -> 3
//    @Fetch(FetchMode.SUBSELECT)
//    1 + N -> 1 + 1 -> 2
    @OneToMany(mappedBy = "receiver")
    private List<Payment> payments = new ArrayList<>();

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

    public String fullName() {
        return getPersonalInfo().getFirstname() + " " + getPersonalInfo().getLastname();
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
