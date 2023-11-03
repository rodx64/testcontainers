package com.javaguides.springboot.repository;

import com.javaguides.springboot.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    @Query("select e from Employee e " +
            "where e.firstName = ?1 " +
            "and e.lastName = ?2 ")
    Employee findByJPQL(String firsName, String lastName);


    @Query("select e from Employee e " +
            "where e.firstName =:firstName " +
            "and e.lastName =:lastName ")
    Employee findByJPQLNamedParams(@Param("firstName") String firsName, @Param("lastName") String lastName);


    @Query(value = "select * from employees e " +
            "where e.first_name = ?1 " +
            "and e.last_name = ?2 " ,
            nativeQuery = true)
    Employee findByNativeQuery(String firstName, String lastName);

    @Query(value = "select * from employees e " +
            "where e.first_name = :firstName " +
            "and e.last_name = :lastName " ,
            nativeQuery = true)
    Employee findByNativeQueryNamedParams(@Param("firstName") String firsName, @Param("lastName") String lastName);
}
