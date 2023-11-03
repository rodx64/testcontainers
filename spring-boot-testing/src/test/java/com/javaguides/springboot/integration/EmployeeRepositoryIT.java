package com.javaguides.springboot.integration;

import com.javaguides.springboot.integration.config.ContainerBaseTest;
import com.javaguides.springboot.model.Employee;
import com.javaguides.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static com.javaguides.springboot.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// to integration test, we need to use the real database (not a mocked one), in our case MySQL
// this config help us to switch it (@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE))
public class EmployeeRepositoryIT extends ContainerBaseTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @DisplayName("Save Employee Operation")
    @Test
    void givenEmployeeObject_whenSave_thenReturnSavedEmployeeObject(){
        // given

        // when
        Employee savedEmployee = employeeRepository.save(employee1);

        // then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);
    }


    @DisplayName("FindAll Employee Operation")
    @Test
    void givenEmployees_whenFindAll_thenEmployeesList(){
        // given
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        // when
        List<Employee> employeeList = employeeRepository.findAll();

        // then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList.size()).isEqualTo(2);
    }

    @DisplayName("FindById Employee Operation")
    @Test
    void givenEmployeeObject_whenFindById_thenEmployeeObject(){
        // given
        employeeRepository.save(employee1);

        // when
        Employee savedEmployee = employeeRepository.findById(employee1.getId()).get();

        // then
        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("FindByEmail Employee Operation")
    @Test
    void givenEmployeeObject_whenFindByEmail_thenEmployeeObject(){
        // given
        employeeRepository.save(employee1);

        // when
        Employee savedEmployee = employeeRepository.findByEmail(email1).get();

        // then
        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("Update Employee Operation")
    @Test
    void givenEmployeeObject_whenUpdateEmployee_thenEmployeeUpdatedObject(){
        // given
        employeeRepository.save(employee1);

        // when
        Employee managedEmployee = employeeRepository.findById(employee1.getId()).get();

        managedEmployee.setEmail("rodrigo.updated@gmail.com");
        managedEmployee.setLastName("rodrigo updated");
        employeeRepository.save(managedEmployee);

        Employee updatedEmployee = employeeRepository.findByEmail("rodrigo.updated@gmail.com").get();

        // then
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getLastName()).isEqualTo("rodrigo updated");
    }

    @DisplayName("Delete Employee Operation")
    @Test
    void givenEmployeeObject_whenDeleteEmployee_thenRemoveEmployee(){
        // given
        employeeRepository.save(employee1);

        // when
        employeeRepository.delete(employee1);
        Optional<Employee> employeeOptional = employeeRepository.findById(employee1.getId());
        // then
        assertThat(employeeOptional).isEmpty();
    }

    @DisplayName("findByJPQL Employee Operation")
    @Test
    void givenEmployeeObject_whenFindByJPQL_thenEmployee(){
        // given
        employeeRepository.save(employee1);

        // when
        Employee employeeManaged = employeeRepository.findByJPQL(firstName, lastName1);

        // then
        assertThat(employeeManaged).isNotNull();
    }

    @DisplayName("findByJPQLNamedParams Employee Operation")
    @Test
    void givenEmployeeObject_whenFindByJPQLNamedParams_thenEmployee(){
        // given
        employeeRepository.save(employee1);

        // when
        Employee employeeManaged = employeeRepository.findByJPQLNamedParams(firstName, lastName1);

        // then
        assertThat(employeeManaged).isNotNull();
    }

    @DisplayName("findByNativeQuery Employee Operation")
    @Test
    void givenEmployeeObject_whenFindByNativeQuery_thenEmployee(){
        // given
        employeeRepository.save(employee1);

        // when
        Employee employeeManaged = employeeRepository.findByNativeQuery(firstName, lastName1);

        // then
        assertThat(employeeManaged).isNotNull();
    }

    @DisplayName("findByNativeQueryNamedParams Employee Operation")
    @Test
    void givenEmployeeObject_whenFindByNativeQueryNamedParams_thenEmployee(){
        // given
        employeeRepository.save(employee1);

        // when
        Employee employeeManaged = employeeRepository.findByNativeQueryNamedParams(firstName, lastName1);

        // then
        assertThat(employeeManaged).isNotNull();
    }
}
