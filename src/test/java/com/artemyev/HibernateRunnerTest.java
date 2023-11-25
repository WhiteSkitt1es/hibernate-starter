package com.artemyev;

import com.artemyev.entity.*;

import com.artemyev.util.HibernateUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkOrhanRemoval() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = session.getReference(Company.class, 3);
            company.getUsers().removeIf(users -> users.getId().equals(2L));

            session.getTransaction().commit();
        }
    }
    @Test
    void checkLazyInitialization() {
        Company company = null;
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            company = session.getReference(Company.class, 3);
//            Hibernate.initialize(company.getUsers());
//            System.out.println();

            session.getTransaction().commit();
        }
        Set<Users> users = company.getUsers();
//        for (Users user: users){
//            System.out.println(user);
//        }
        System.out.println(users.size());

    }

    @Test
    void getCompanyById() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = session.get(Company.class, 3);
            Hibernate.initialize(company.getUsers());
            System.out.println();

            session.getTransaction().commit();
        }
    }

    @Test
    void deleteCompany() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = session.get(Company.class, 3);
            session.remove(company);

            session.getTransaction().commit();
        }
    }

    @Test
    void addUserToNewCompany() { // one to many
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = Company.builder()
                    .name("Yandex")
                    .build();

            Users users = Users.builder()
                    .username("paul@gmail.com")
                    .personalInfo(PersonalInfo.builder()
                            .firstname("Paul")
                            .lastname("Artemev")
                            .birthday(new Birthday(LocalDate.of(1997, 1, 1)))
                            .build())
                    .role(Role.USER)
                    .build();
            company.addUser(users);

            session.persist(company);

            session.getTransaction().commit();
        }
    }

    @Test
    void oneToMany() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = session.get(Company.class, 3);
            System.out.println(company);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();

        Class<Users> usersClass = Users.class;
        Constructor<Users> constructor = usersClass.getConstructor();
        Users users = constructor.newInstance();
        Field declaredField = usersClass.getDeclaredField("id");
        declaredField.setAccessible(true);
        declaredField.set(users, resultSet.getString(1));
    }

    @Test
    void checkReflectionApi() throws SQLException {
        Users users = Users.builder()
                .username("pavel@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .firstname("Pavel")
                        .lastname("Artem'yev")
                        .birthday(new Birthday(LocalDate.of(1997, 1, 1)))
                        .build())
                .role(Role.ADMIN)
                .build();

        String sql = """
                insert
                into
                    %s
                    (%s)
                values
                    (%s)
                """;

        String tableName = Optional.ofNullable(users.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(users.getClass().getName());

        String columnNames = Arrays.stream(users.getClass().getDeclaredFields())
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(Collectors.joining(", "));

        String columnValues = Arrays.stream(users.getClass().getDeclaredFields())
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        Connection connection = null;
        PreparedStatement preparedStatement = connection
                .prepareStatement(sql.formatted(tableName, columnNames, columnValues));

        preparedStatement.setObject(1, users.getUsername());
        preparedStatement.setObject(2, users.getPersonalInfo().getFirstname());
        preparedStatement.setObject(3, users.getPersonalInfo().getLastname());
        preparedStatement.setObject(4, users.getPersonalInfo().getBirthday());
        preparedStatement.setObject(5, users.getRole());

        preparedStatement.executeUpdate();
    }
}