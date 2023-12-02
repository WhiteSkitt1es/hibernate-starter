package com.artemyev.util;

import com.artemyev.entity.*;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;

@UtilityClass
public class TestDataImporter {
    public void importData(SessionFactory sessionFactory) {
        @Cleanup Session session = sessionFactory.openSession();

        Company microsoft = saveCompany(session, "Microsoft");
        Company apple = saveCompany(session, "Apple");
        Company google = saveCompany(session, "Google");

        User billGates = saveUser(session, "Bill", "Gates",
                LocalDate.of(1955, 10, 28), microsoft);
        User steveJobs = saveUser(session, "Steve", "Jobs",
                LocalDate.of(1955, 2, 24), apple);
        User sergeyBrin = saveUser(session, "Sergey", "Brin",
                LocalDate.of(1973, 8, 21), google);
        User timCook = saveUser(session, "Tim", "Cook",
                LocalDate.of(1960, 11, 1), apple);
        User dianeGreene = saveUser(session, "Diane", "Greene",
                LocalDate.of(1955, 1, 1), google);

        savePayment(session, billGates, 100);
        savePayment(session, billGates, 300);
        savePayment(session, billGates, 500);

        savePayment(session, steveJobs, 250);
        savePayment(session, steveJobs, 600);
        savePayment(session, steveJobs, 500);

        savePayment(session, timCook, 400);
        savePayment(session, timCook, 300);

        savePayment(session, sergeyBrin, 500);
        savePayment(session, sergeyBrin, 500);
        savePayment(session, sergeyBrin, 500);

        savePayment(session, dianeGreene, 300);
        savePayment(session, dianeGreene, 300);
        savePayment(session, dianeGreene, 300);
    }
    private Company saveCompany(Session session, String name) {
        session.beginTransaction();
        Company company = Company.builder()
                .name(name)
                .build();
        session.persist(company);
        session.getTransaction().commit();

        return company;
    }

    private User saveUser(Session session,
                          String firstName,
                          String lastName,
                          LocalDate birthday,
                          Company company) {
        session.beginTransaction();
        User user = User.builder()
                .username(firstName + lastName)
                .personalInfo(PersonalInfo.builder()
                        .firstname(firstName)
                        .lastname(lastName)
                        .birthday(new Birthday(birthday))
                        .build())
                .company(company)
                .build();
        session.persist(user);
        session.getTransaction().commit();

        return user;
    }

    private void savePayment(Session session, User user, Integer amount) {
        session.beginTransaction();
        Payment payment = Payment.builder()
                .receiver(user)
                .amount(amount)
                .build();
        session.persist(payment);
        session.getTransaction().commit();
    }
}
