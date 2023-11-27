package com.artemyev.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class Manager extends EntityUser{

    private String projectName;

    @Builder
    public Manager(Long id, String username, PersonalInfo personalInfo, Role role, Company company, Profile profile, List<UsersChat> usersChats, String projectName) {
        super(id, username, personalInfo, role, company, profile, usersChats);
        this.projectName = projectName;
    }
}
