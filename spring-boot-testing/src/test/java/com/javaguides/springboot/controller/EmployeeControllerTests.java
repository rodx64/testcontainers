package com.javaguides.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaguides.springboot.model.Employee;
import com.javaguides.springboot.service.EmployeeService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static com.javaguides.springboot.TestUtils.employee1;
import static com.javaguides.springboot.TestUtils.employee2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest // Context that contains only the necessary beans for testing
class EmployeeControllerTests {

    @Autowired
    private MockMvc mockMvc; // to call Rest Api's

    @MockBean // Create a mock instance and put it to the WebMvcTest context
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;


    @DisplayName("Create Employee Operation")
    @Test
    void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        // given (stubbing)

        given(employeeService.saveEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0)); // Get the first and only (in this case) argument passed

        // when
        var postContent = MockMvcRequestBuilders
                .post("/api/employees/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1));

        ResultActions result = mockMvc.perform(postContent);


        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(employee1.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employee1.getLastName())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(employee1.getEmail())))
                .andDo(print());

    }

    @DisplayName("GetAll Employees Operation")
    @Test
    void givenListOfEmployees_whenGetAllEmployees_thenReturnsEmployeesList() throws Exception {
        // given (stubbing)
        List<Employee> employeeList = List.of(employee1, employee2);

        given(employeeService.getAllEmployees()).willReturn(employeeList);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/getAll"));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(employeeList.size())));
    }

    @DisplayName("Positive Scenario - GetById Employee Operation")
    @Test
    void givenEmployeeId_whenGetById_thenReturnEmployeeObject() throws Exception {
        // given (stubbing)
        given(employeeService.getEmployeeById(1)).willReturn(Optional.ofNullable(employee1));

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/getById/{id}", employee1.getId()));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(employee1.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employee1.getLastName())))
                .andExpect(jsonPath("$.email", CoreMatchers.is(employee1.getEmail())));

    }

    @DisplayName("Negative Scenario - GetById Employee Operation")
    @Test
    void givenInvalidEmployeeId_whenGetById_thenReturnEmployeeObject() throws Exception {
        // given (stubbing)
        given(employeeService.getEmployeeById(3)).willReturn(Optional.empty());

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/getById/{id}", 3));

        // then
        result.andExpect(status().is4xxClientError()).andDo(print());
    }

    @DisplayName("Positive Scenario - Update Employee Operation")
    @Test
    void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() throws Exception {
        var employeeId = 1;
        // given (stubbing)
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(employee1));
        given(employeeService.updateEmployee(any(Employee.class)))
                .willAnswer(invocation -> invocation.getArgument(0));


        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/employees/update/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee2)))
                ;

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employee2.getLastName())));
    }

    @DisplayName("Negative Scenario - Update Employee Operation")
    @Test
    void givenEmptyEmployeeObject_whenUpdateEmployee_thenReturnNotFoundStatusCode() throws Exception {
        var employeeId = 1;
        // given (stubbing)
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());
        given(employeeService.updateEmployee(any(Employee.class)))
                .willAnswer(invocation -> invocation.getArgument(0));


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

    @DisplayName("Delete Employee Operation")
    @Test
    void givenAValidEmployeeId_whenDeletedEmployee_thenReturn2xx() throws Exception {
        // given (stubbing)
        long employeeId = 1L;
        willDoNothing().given(employeeService).deleteEmployee(employeeId);

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/employees/delete/{id}", employeeId));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }
}