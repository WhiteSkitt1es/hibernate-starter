package com.artemyev;

import com.artemyev.entity.Birthday;
import com.artemyev.entity.Role;
import com.artemyev.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
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
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();

        Class<Users> usersClass = Users.class;
        Constructor<Users> constructor = usersClass.getConstructor();
        Users users = constructor.newInstance();
        Field declaredField = usersClass.getDeclaredField("username");
        declaredField.setAccessible(true);
        declaredField.set(users, resultSet.getString(1));
    }
    @Test
    void checkReflectionApi() throws SQLException {
        Users users = Users.builder()
                .username("pavel@gmail.com")
                .firstname("Pavel")
                .lastname("Artem'yev")
                .birthDate(new Birthday(LocalDate.of(1997, 1, 1)))
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
        preparedStatement.setObject(2, users.getFirstname());
        preparedStatement.setObject(3, users.getLastname());
        preparedStatement.setObject(4, users.getBirthDate());
        preparedStatement.setObject(5, users.getRole());

        preparedStatement.executeUpdate();
    }
}