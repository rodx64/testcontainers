package com.javaguides.springboot;

import com.javaguides.springboot.model.Employee;

import java.util.ArrayList;
import java.util.List;

public abstract class TestUtils {

    public static final String firstName = "Rodrigo";
    public static final String lastName1 = "Rodrigo1";
    public static final String lastName2 = "Rodrigo2";
    public static final String email1 = "rodrigo1@gmail.com";
    public static final String email2 = "rodrigo2@gmail.com";

    public static Employee employee1 =
            Employee.builder()
            .id(1L)
            .firstName(firstName)
                .lastName(lastName1)
                .email(email1)
            .build();

    public static Employee employee2 =
            Employee.builder()
            .id(2L)
            .firstName(firstName)
                .lastName(lastName2)
                .email(email2)
            .build();

    public static List<Employee> employeeList = new ArrayList<>(){{
        add(employee1);
        add(employee2);
    }};

}
