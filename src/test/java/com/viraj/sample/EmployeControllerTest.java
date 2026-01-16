package com.viraj.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viraj.sample.controller.EmployeeController;
import com.viraj.sample.entity.Employee;
import com.viraj.sample.service.EmployeeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ActiveProfiles("test")
public class EmployeControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeServiceMock;
    @Test
    public void saveEmployeeShouldSaveEmployee() throws Exception {
        // Arrange
        Employee request = new Employee("Ana", "IT");
        Employee saved = new Employee("Ana", "IT");

        when(employeServiceMock.saveEmployee(any(Employee.class))).thenReturn(saved);

        // Act + Assert
        mockMvc.perform((RequestBuilder) post("/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.valueOf(objectMapper.writeValueAsString(request))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.id").value(1))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Ana"))
                .andExpect((ResultMatcher) jsonPath("$.department").value("IT"));

        verify(employeServiceMock, times(1)).saveEmployee(any(Employee.class));
        verifyNoMoreInteractions(employeServiceMock);
    }

    @Test
    public void updateEmployeeShouldUpdateEmployee() throws Exception {
        // Arrange
        Employee request = new Employee("Juan", "QA");
        Employee updated = new Employee("Juan", "QA");

        when(employeServiceMock.updateEmployee(any(Employee.class))).thenReturn(updated);

        // Act + Assert
        mockMvc.perform((RequestBuilder) put("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.valueOf(objectMapper.writeValueAsString(request))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.id").value(10))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Juan"))
                .andExpect((ResultMatcher) jsonPath("$.department").value("QA"));

        verify(employeServiceMock, times(1)).updateEmployee(any(Employee.class));
        verifyNoMoreInteractions(employeServiceMock);
    }

    @Test
    public void getAllEmployeesShouldReturnAll() throws Exception {
        // Arrange
        List<Employee> employees = List.of(
                new Employee("Ana", "IT"),
                new Employee("Luis", "HR")
        );

        when(employeServiceMock.getAllEmployees()).thenReturn(employees);

        // Act + Assert
        mockMvc.perform(get("/getall")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.length()").value(2))
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1))
                .andExpect((ResultMatcher) jsonPath("$[0].name").value("Ana"))
                .andExpect((ResultMatcher) jsonPath("$[1].id").value(2))
                .andExpect((ResultMatcher) jsonPath("$[1].name").value("Luis"));

        verify(employeServiceMock, times(1)).getAllEmployees();
        verifyNoMoreInteractions(employeServiceMock);
    }

    @Test
    public void getEmployeeShouldRetunEmployee() throws Exception {
        // Arrange
        long employeeId = 5L;
        Employee employee = new Employee("Maria", "Finance");

        when(employeServiceMock.getEmployee((employeeId))).thenReturn(employee);

        // Act + Assert
        mockMvc.perform(get("/getone/{employeeId}", employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.id").value(5))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Maria"))
                .andExpect((ResultMatcher) jsonPath("$.department").value("Finance"));

        verify(employeServiceMock, times(1)).getEmployee(employeeId);
        verifyNoMoreInteractions(employeServiceMock);
    }

    @Test
    public void deleteEmployeeShouldRetunEmployee() throws Exception {
        // Arrange
        long employeeId = 7L;
        doNothing().when(employeServiceMock).deleteEmployee(employeeId);

        // Act + Assert
        mockMvc.perform(delete("/delete/{employeeId}", employeeId))
                .andExpect(status().isOk()); // si tu controller devuelve void, normalmente será 200

        verify(employeServiceMock, times(1)).deleteEmployee(employeeId);
        verifyNoMoreInteractions(employeServiceMock);
    }
}
