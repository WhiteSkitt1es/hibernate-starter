package com.artemyev.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.SortNatural;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString(exclude = "users")
@Builder
@Entity
//@Table(name = "company", schema = "public")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("username DESC, personalInfo.lastname ASC")
    @OrderColumn(name = "id")
    private List<User> users = new ArrayList<>();
//    @SortNatural
//    @SortComparator()
//    @JoinColumn(name = "company_id")
//    private Set<User> users = new TreeSet<>();
//    @MapKey(name = "username")
//    @SortNatural
//    @SortComparator()
//    private Map<String, User> users = new HashMap<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "company_locale", joinColumns = @JoinColumn(name = "company_id"))
    private List<LocaleInfo> locales = new ArrayList<>();

//    @Column(name = "description")
//    private List<String> locales = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        user.setCompany(this);
    }
}


