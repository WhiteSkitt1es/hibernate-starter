package com.artemyev.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
@ToString(exclude = "company")
@Builder
@Entity
//@Table(name = "users", schema = "public")
public class Users {

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

//    @Column(name = "info")
//    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
//    private String info;
}
