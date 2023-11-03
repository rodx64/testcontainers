package com.javaguides.springboot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaguides.springboot.integration.config.ContainerBaseTest;
import com.javaguides.springboot.repository.EmployeeRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.javaguides.springboot.TestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerIT extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp(){
        employeeRepository.deleteAll();
    }

    @DisplayName("Integration Test - Create employee operation")
    @Test
    void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        // given
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/employees/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1)));

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(employee1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee1.getLastName())))
                .andExpect(jsonPath("$.email", is(employee1.getEmail())));

    }

    @DisplayName("Integration Test - GetAll employee operation")
    @Test
    void givenListOfEmployees_whenGetAllEmployees_thenReturnsEmployeesList() throws Exception {
        // given
        employeeRepository.saveAll(employeeList);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/getAll"));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(employeeList.size())));
    }

    @DisplayName("Integration Test - Positive Scenario - GetById Employee Operation")
    @Test
    void givenEmployeeId_whenGetById_thenReturnEmployeeObject() throws Exception {
        // given (stubbing)
        employeeRepository.save(employee1);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/getById/{id}", employee1.getId()));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(employee1.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employee1.getLastName())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(employee1.getEmail())));

    }

    @DisplayName("Integration Test - Negative Scenario - GetById Employee Operation")
    @Test
    void givenInvalidEmployeeId_whenGetById_thenReturnEmployeeObject() throws Exception {
        // given
        final long id = 3;
        employeeRepository.save(employee1);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/getById/{id}", id));

        // then
        result.andExpect(status().is4xxClientError())
              .andDo(print());
    }

    @DisplayName("Integration Test - Positive Scenario - Update Employee Operation")
    @Test
    void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() throws Exception {
        // given
        employeeRepository.save(employee1);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/employees/update/{id}", employee1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee2)))
                ;

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(employee2.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employee2.getLastName())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(employee2.getEmail())));
    }

    @DisplayName("Integration Test - Negative Scenario - Update Employee Operation")
    @Test
    void givenEmptyEmployeeObject_whenUpdateEmployee_thenReturnNotFoundStatusCode() throws Exception {
        var employeeId = 3;
        // given
        employeeRepository.save(employee1);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/employees/update/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee2)))
                ;

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @DisplayName("Integration Test - Delete Employee Operation")
    @Test
    void givenAValidEmployeeId_whenDeletedEmployee_thenReturn2xx() throws Exception {
        // given
        long employeeId = 1L;
        employeeRepository.save(employee1);

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/employees/delete/{id}", employee1.getId()));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }
}

