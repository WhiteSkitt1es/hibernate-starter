package com.artemyev.entity;

import jakarta.persistence.criteria.JoinType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.criteria.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */
    public List<User> findAll(Session session) {
//        HQL
//        return session.createQuery("select u from User u", User.class)
//                .list();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<User> criteria = cb.createQuery(User.class);
        JpaRoot<User> user = criteria.from(User.class);
        criteria.select(user);
        return session.createQuery(criteria)
                .list();
    }

    /**
     * Возвращает всех сотрудников с указанным именем
     */
    public List<User> findAllByFirstName(Session session, String firstname) {
//        HQL
//        return session.createQuery("select u from User u " +
//                                   "where u.personalInfo.firstname = :firstname", User.class)
//                .setParameter("firstname", firstname)
//                .list();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<User> criteria = cb.createQuery(User.class);
        JpaRoot<User> user = criteria.from(User.class);
        criteria.select(user).where(cb.equal(user.get(User_.PERSONAL_INFO).get(PersonalInfo_.FIRSTNAME), firstname));
        return session.createQuery(criteria)
                .list();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
//        HQL
//        return session.createQuery("select u from User u order by u.personalInfo.birthday", User.class)
//                .setMaxResults(limit)
//                .list();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<User> criteria = cb.createQuery(User.class);
        JpaRoot<User> user = criteria.from(User.class);
        criteria.select(user).orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.birthday)));
        return session.createQuery(criteria)
                .setMaxResults(limit)
                .list();
    }

    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
//        HQL
//        return session.createQuery("select u from Company c " +
//                                   "join c.users u where c.name = :companyName", User.class)
//                .setParameter("companyName", companyName)
//                .list();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<User> criteria = cb.createQuery(User.class);
        JpaRoot<Company> company = criteria.from(Company.class);
        JpaListJoin<Company, User> users = company.join(Company_.users);
        criteria.select(users).where(cb.equal(company.get(Company_.name), companyName));
        return session.createQuery(criteria)
                .list();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именами
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
//        HQL
//        return session.createQuery("select p from Payment p " +
//                                   "join p.receiver u " +
//                                   "join u.company c " +
//                                   "where c.name = :companyName " +
//                                   "order by u.personalInfo.firstname, p.amount", Payment.class)
//                .setParameter("companyName", companyName)
//                .list();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<Payment> criteria = cb.createQuery(Payment.class);
        JpaRoot<Payment> payment = criteria.from(Payment.class);
        JpaJoin<Payment, User> user = payment.join(Payment_.receiver);
        JpaJoin<User, Company> company = user.join(User_.company);
        criteria.select(payment).where(cb.equal(company.get(Company_.name), companyName))
                .orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)),
                        cb.asc(payment.get(Payment_.amount)));
        return session.createQuery(criteria)
                .list();
    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанным именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
//        HQL
//        return session.createQuery("select avg(p.amount) from Payment p " +
//                                   "join p.receiver u " +
//                                   "where u.personalInfo.firstname = :firstName " +
//                                   "and u.personalInfo.lastname = :lastName", Double.class)
//                .setParameter("firstName", firstName)
//                .setParameter("lastName", lastName)
//                .uniqueResult();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<Double> criteria = cb.createQuery(Double.class);
        JpaRoot<Payment> payment = criteria.from(Payment.class);
        JpaJoin<Payment, User> user = payment.join(Payment_.receiver);
        criteria.select(cb.avg(payment.get(Payment_.amount))).where(
                cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName),
                cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.lastname), lastName)
        );
        return session.createQuery(criteria)
                .uniqueResult();
    }

    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех ее сотрудников. Компании упорядочены по названию.
     */
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
//        HQL
//        return session.createQuery("select c.name, avg(p.amount) from Company c " +
//                                   "join c.users u " +
//                                   "join u.payments p " +
//                                   "group by c.name " +
//                                   "order by c.name", Object[].class)
//                .list();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
        JpaRoot<Company> company = criteria.from(Company.class);
        JpaListJoin<Company, User> user = company.join(Company_.users, JoinType.INNER);
        JpaListJoin<User, Payment> payment = user.join(User_.payments);
        criteria.multiselect(company.get(Company_.name), cb.avg(payment.get(Payment_.amount)))
                .groupBy(company.get(Company_.name))
                .orderBy(cb.asc(company.get(Company_.name)));
        return session.createQuery(criteria)
                .list();
    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только тех сотрудников, чей средний
     * больше среднего размера выплат всех сотрудников.
     * Упорядочить по имени сотрудника.
     */
    public List<Object[]> isItPossible(Session session) {
//        HQL
//        return session.createQuery("select u, avg(p.amount) from User u " +
//                                   "join u.payments p " +
//                                   "group by u " +
//                                   "having avg(p.amount) > (select avg(p.amount) from Payment p) " +
//                                   "order by u.personalInfo.firstname", Object[].class)
//                .list();
//        Criteria API
        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
        JpaCriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
        JpaRoot<User> user = criteria.from(User.class);
        JpaListJoin<User, Payment> payment = user.join(User_.payments);
        JpaSubQuery<Double> subquery = criteria.subquery(Double.class);
        JpaRoot<Payment> paymentSubquery = subquery.from(Payment.class);
        criteria.multiselect(user, cb.avg(payment.get(Payment_.amount)))
                .groupBy(user.get(User_.id))
                .having(cb.gt(cb.avg(payment.get(Payment_.amount)),
                        subquery.select(cb.avg(paymentSubquery.get(Payment_.amount)))))
                .orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)));
        return session.createQuery(criteria)
                .list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
