package com.artemyev;

import com.artemyev.entity.*;

import com.artemyev.util.HibernateTestUtil;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkH2() {
        try (SessionFactory sessionFactory = HibernateTestUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = Company.builder()
                    .name("Yandex")
                    .build();
            session.persist(company);

            Programmer programmer = Programmer.builder()
                    .username("pavelprog@gmail.com")
                    .language(Language.JAVA)
                    .company(company)
                    .build();
            session.persist(programmer);

            Manager manager = Manager.builder()
                    .username("ivanmanag@gmail.com")
                    .projectName("Starter")
                    .company(company)
                    .build();
            session.persist(manager);
            session.flush();

            session.clear();

            Programmer programmer1 = session.get(Programmer.class, 1L);
            EntityUser manager1 = session.get(EntityUser.class, 2L);
            System.out.println(programmer1 + ", " + manager1);


            session.getTransaction().commit();
        }
    }

    @Test
    void getOrderUsers() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = session.get(Company.class, 3L);
            company.getUsers().forEach(System.out::println);

            session.getTransaction().commit();
        }
    }

    @Test
    void localeInfo() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            Company company = session.get(Company.class, 3L);
            company.getLocales().add(LocaleInfo.of("ru", "Описание на русском"));
            company.getLocales().add(LocaleInfo.of("en", "English description"));

            session.getTransaction().commit();
        }
    }

    @Test
    void checkManyToMany() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            User user = session.get(User.class, 4L);
            Chat chat = session.get(Chat.class, 1L);

            UsersChat usersChat = UsersChat.builder()
                    .createdAt(Instant.now())
                    .createdBy(user.getUsername())
                    .build();
            usersChat.setUser(user);
            usersChat.setChat(chat);

            session.persist(usersChat);

//            Chat workChat = Chat.builder()
//                    .name("workChat")
//                    .build();
//            user.addChat(workChat);
//            session.persist(workChat);


            session.getTransaction().commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            User user = User.builder()
                    .username("pavel@gmail.com")
                    .personalInfo(PersonalInfo.builder()
                            .firstname("Pavel")
                            .lastname("Artemyev")
                            .birthday(new Birthday(LocalDate.of(1997, 1, 1)))
                            .build())
                    .role(Role.ADMIN)
                    .build();

            Profile profile = Profile.builder()
                    .street("Grunina 6")
                    .language("ru")
                    .build();
            profile.setUser(user);

            session.persist(user);

            session.getTransaction().commit();
        }
    }

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
        List<User> users = company.getUsers();
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

            User user = User.builder()
                    .username("paul@gmail.com")
                    .personalInfo(PersonalInfo.builder()
                            .firstname("Paul")
                            .lastname("Artemev")
                            .birthday(new Birthday(LocalDate.of(1997, 1, 1)))
                            .build())
                    .role(Role.USER)
                    .build();
            company.addUser(user);

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

        Class<User> usersClass = User.class;
        Constructor<User> constructor = usersClass.getConstructor();
        User user = constructor.newInstance();
        Field declaredField = usersClass.getDeclaredField("id");
        declaredField.setAccessible(true);
        declaredField.set(user, resultSet.getString(1));
    }

    @Test
    void checkReflectionApi() throws SQLException {
        User user = User.builder()
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

        String tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        String columnNames = Arrays.stream(user.getClass().getDeclaredFields())
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(Collectors.joining(", "));

        String columnValues = Arrays.stream(user.getClass().getDeclaredFields())
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        Connection connection = null;
        PreparedStatement preparedStatement = connection
                .prepareStatement(sql.formatted(tableName, columnNames, columnValues));

        preparedStatement.setObject(1, user.getUsername());
        preparedStatement.setObject(2, user.getPersonalInfo().getFirstname());
        preparedStatement.setObject(3, user.getPersonalInfo().getLastname());
        preparedStatement.setObject(4, user.getPersonalInfo().getBirthday());
        preparedStatement.setObject(5, user.getRole());

        preparedStatement.executeUpdate();
    }
}