package com.artemyev;

import com.artemyev.entity.*;
//import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.artemyev.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;

@Slf4j
public class HibernateRunner {

//    private static final Logger logger = LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) {

        Company company = Company.builder()
                .name("Google")
                .build();

        User user = User.builder()
                .username("pavel@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .firstname("Pavel")
                        .lastname("Artemyev")
                        .birthday(LocalDate.of(1997, 1, 1))
                        .build())
                .role(Role.ADMIN)
                .company(company)
                .build();


        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            Session session1 = sessionFactory.openSession();
            try (session1) {
                Transaction transaction = session1.beginTransaction();

                session1.persist(company);
                session1.persist(user);

                session1.getTransaction().commit();
            }
        }
        //-------------------------------------------------------------------------------------------------
        /**
         *         Configuration configuration = new Configuration();
         *         configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
         *         configuration.addAttributeConverter(new BirthdayConverter());
         *         configuration.registerTypeOverride(new JsonBinaryType());
         *         configuration.configure();
         *
         *         try (SessionFactory sessionFactory = configuration.buildSessionFactory();
         *              Session session = sessionFactory.openSession()) {
         *             session.beginTransaction();
         *
         *             Users users = Users.builder()
         *                     .username("pavel@gmail.com")
         *                     .firstname("Pavel")
         *                     .lastname("Artem'yev")
         *                     .birthDate(new Birthday(LocalDate.of(1997, 1, 1)))
         *                     .role(Role.ADMIN)
         *                     .info("""
         *                             {
         *                                 "name": "Pavel",
         *                                 "id": 1
         *                             }
         *                             """)
         *                     .build();
         *             session.persist(users); // add entity to the table
         *             session.update(users); // update information entity
         *             session.saveOrUpdate(users); // save or update information entity
         *             session.delete(users); // delete entity from the table
         *             Users user = session.get(Users.class, "pavel@gmail.com");// get entity by id
         *             user.setLastname("Lavrov");
         *             session.flush();
         *
         *             session.evict(users); // remove entity from cache
         *             session.clear(); // remove all entity from cache
         *
         *             session.getTransaction().commit();
         */
        //---------------------------------------------------------------------------------------------
        /**
         *  Users users = Users.builder()
         *                 .username("pavel@gmail.com")
         *                 .personalInfo(PersonalInfo.builder()
         *                         .firstname("Pavel")
         *                         .lastname("Artemyev")
         *                         .birthday(new Birthday(LocalDate.of(1997, 1, 1)))
         *                         .build())
         *                 .role(Role.ADMIN)
         *                 .build();
         *
         *         log.info("User entity is in transient state, object: {}", users);
         *
         *         try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
         *             Session session1 = sessionFactory.openSession();
         *             try (session1) {
         *                 Transaction transaction = session1.beginTransaction();
         *                 log.trace("Transaction is created, {}", transaction);
         *
         *                 session1.merge(users);
         *                 log.trace("User is in persistent state: {}, session {}", users, session1);
         *
         *                 session1.getTransaction().commit();
         *             }
         *             log.warn("User is in detached state: {}, session is closed {}", users, session1);
         *             try (Session session2 = sessionFactory.openSession()) {
         *                 session2.beginTransaction();
         *
         *                session2.remove(users);
         *                 refresh/merge
         *                 session2.refresh(users); //read the entity data again
         *
         *                 session2.merge(users); //same as refresh only in reverse
         *
         *                 session2.getTransaction().commit();
         *             }
         *         } catch (Exception exception) {
         *             log.error("Exception occurred", exception);
         *             throw exception;
         *         }
         */
    }
}
