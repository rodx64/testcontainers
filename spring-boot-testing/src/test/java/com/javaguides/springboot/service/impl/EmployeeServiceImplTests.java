package com.javaguides.springboot.service.impl;

import com.javaguides.springboot.exception.ResourceNotFoundException;
import com.javaguides.springboot.model.Employee;
import com.javaguides.springboot.repository.EmployeeRepository;
import com.javaguides.springboot.service.EmployeeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.javaguides.springboot.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;


    @DisplayName("save Employee Operation")
    @Test
    void givenEmployeeObject_whenSaveEmployee_thenReturnSavedEmployee(){
        // given

        // Stub call method save()
        given(employeeRepository.save(employee1)).willReturn(employee1);

        // when
        Employee savedEmployee = employeeService.saveEmployee(employee1);

        // then
        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("save Employee Operation throwing exception")
    @Test
    void givenExistingEmailEmployeeObject_whenSaveEmployee_thenThrowsException(){
        // given

        // Stub call method findByEmail()
        given(employeeRepository.findByEmail(employee1.getEmail())).willReturn(Optional.of(employee1));

        // Unnecessary stubbing because the exception will be thrown before the save method is called
        // given(employeeRepository.save(employee1)).willReturn(employee1);

        // when
        assertThrows(ResourceNotFoundException.class, () -> employeeService.saveEmployee(employee1));

        // then
        // Verifying if the save method will never be called
        verify(employeeRepository, never()).save(any(Employee.class));

    }

    @DisplayName("findAll Employee Operation")
    @Test
    void givenEmployeesList_whenGetAllEmployees_thenReturnEmployeesList(){
        // given
        given(employeeRepository.findAll()).willReturn(List.of(employee1, employee2));

        // when
        List<Employee> employeeList = employeeService.getAllEmployees();

        // then
        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.size()).isEqualTo(2);
        assertThat(employeeList.get(0).getId()).isEqualTo(1L);
        assertThat(employeeList.get(1).getId()).isEqualTo(2L);
        assertThrows(IndexOutOfBoundsException.class, () -> employeeList.get(2));
    }

    @DisplayName("findAll Employee Operation (empty list)")
    @Test
    void givenEmptyEmployeesList_whenGetAllEmployees_thenReturnEmptyEmployeesList(){
        // given
        given(employeeRepository.findAll()).willReturn(List.of());

        // when
        List<Employee> employeeList = employeeService.getAllEmployees();

        // then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isEmpty();
        assertThat(employeeList.size()).isEqualTo(0);
    }

    @DisplayName("findById Employee Operation")
    @Test
    void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject(){
        // given
         given(employeeRepository.findById(1L)).willReturn(Optional.ofNullable(employee1));

        // when
        Employee savedEmployee = employeeService.getEmployeeById(1L).get();

        // then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getEmail()).isEqualTo(email1);
    }

    @DisplayName("findById Invalid Employee Operation")
    @Test
    void givenInvalidEmployeeId_whenGetEmployeeById_thenReturnEmptyOptional(){
        // given
         given(employeeRepository.findById(3L)).willReturn(Optional.empty());

        // when
        Optional<Employee> savedEmployee = employeeService.getEmployeeById(3L);

        // then
        assertThat(savedEmployee).isEmpty();
    }

    @DisplayName("update Employee Operation")
    @Test
    void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee(){
        // given
        given(employeeRepository.save(employee1)).willReturn(employee1);
        employee1.setEmail("updatedEmail@gmail.com");
        employee1.setLastName("updatedLastname");

        // when
        Employee updatedEmployee = employeeService.updateEmployee(employee1);

        // then
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getEmail()).isEqualTo("updatedEmail@gmail.com");
        assertThat(updatedEmployee.getLastName()).isEqualTo("updatedLastname");
    }

    @DisplayName("delete Employee Operation")
    @Test
    void givenEmployeeId_whenDeleteEmployee_thenNothing(){
        // given
        long employeeId = 1L;
        willDoNothing().given(employeeRepository).deleteById(employeeId); // Testing void method

        // when
        employeeService.deleteEmployee(employeeId);

        // then
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }

}