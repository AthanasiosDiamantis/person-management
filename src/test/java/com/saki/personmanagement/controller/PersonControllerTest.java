package com.saki.personmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saki.personmanagement.config.SecurityConfig;
import com.saki.personmanagement.model.Person;
import com.saki.personmanagement.security.CustomUserDetailsService;
import com.saki.personmanagement.security.JwtTokenProvider;
import com.saki.personmanagement.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests for {@link PersonController} using MockMvc.
 * Controller-Tests für {@link PersonController} mit MockMvc.
 *
 * @author saki
 */
@WebMvcTest(PersonController.class)
@DisplayName("PersonController Tests")
@EnableMethodSecurity
@Import(SecurityConfig.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonService personService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private Person testPerson;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setFirstName("Max");
        testPerson.setLastName("Mustermann");
        testPerson.setEmail("max@example.com");
    }

    @Nested
    @DisplayName("GET /api/persons")
    class GetAllPersons {

        @Test
        @DisplayName("should return 200 and list of persons")
        @WithMockUser // Simulates authenticated user / Simuliert authentifizierten User
        void shouldReturn200AndListOfPersons() throws Exception {
            // Given
            when(personService.getAllPersons()).thenReturn(List.of(testPerson));

            // When & Then
            mockMvc.perform(get("/api/persons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].firstName").value("Max"))
                    .andExpect(jsonPath("$[0].lastName").value("Mustermann"));

            verify(personService).getAllPersons();
        }

        @Test
        @DisplayName("should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/persons"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/persons/{id}")
    class GetPersonById {

        @Test
        @DisplayName("should return 200 and person when found")
        @WithMockUser
        void shouldReturn200AndPersonWhenFound() throws Exception {
            // Given
            when(personService.getPersonById(1L)).thenReturn(Optional.of(testPerson));

            // When & Then
            mockMvc.perform(get("/api/persons/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Max"))
                    .andExpect(jsonPath("$.email").value("max@example.com"));
        }

        @Test
        @DisplayName("should return 404 when person not found")
        @WithMockUser
        void shouldReturn404WhenPersonNotFound() throws Exception {
            // Given
            when(personService.getPersonById(99L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/persons/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/persons")
    class CreatePerson {

        @Test
        @DisplayName("should return 201 when person created successfully")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn201WhenPersonCreatedSuccessfully() throws Exception {
            // Given
            when(personService.createPerson(any(Person.class))).thenReturn(testPerson);

            // When & Then
            mockMvc.perform(post("/api/persons")
                            .with(csrf()) // CSRF token for POST requests
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testPerson)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value("Max"));

            verify(personService).createPerson(any(Person.class));
        }

        @Test
        @DisplayName("should return 403 when user has no ADMIN role")
        @WithMockUser(roles = "USER")
        void shouldReturn403WhenUserHasNoAdminRole() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/persons")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testPerson)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/persons/{id}")
    class DeletePerson {

        @Test
        @DisplayName("should return 204 when person deleted")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn204WhenPersonDeleted() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/persons/1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(personService).deletePerson(1L);
        }
    }
}