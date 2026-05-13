package com.saki.personmanagement.service;

import com.saki.personmanagement.model.Person;
import com.saki.personmanagement.repository.OrderRepository;
import com.saki.personmanagement.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PersonService}.
 * Unit-Tests für {@link PersonService}.
 *
 * @author saki
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PersonService")
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PersonService personService;

    private Person testPerson;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setFirstName("Tim");
        testPerson.setLastName("Taler");
        testPerson.setEmail("tim@taler.de");
    }

    @Nested
    @DisplayName("getAllPersons()")
    class GetAllPersons {

        @Test
        @DisplayName("should return all persons from repository")
        void shouldReturnAllPersonsFromRepository() {
            // Given
            when(personRepository.findAll()).thenReturn(List.of(testPerson));

            // When
            List<Person> result = personService.getAllPersons();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getFirstName()).isEqualTo("Tim");
            verify(personRepository).findAll(); // verify that the mock-repository was called at least once
        }

        @Test
        @DisplayName("should return empty list when no persons exist")
        void shouldReturnEmptyListWhenNoPersonsExist() {
            // Given
            when(personRepository.findAll()).thenReturn(List.of());

            // When
            List<Person> result = personService.getAllPersons();

            // Then
            assertThat(result).isEmpty();
            verify(personRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getPersonById()")
    class GetPersonById {

        @Test
        @DisplayName("should return person when found")
        void shouldReturnPersonWhenFound() {
            // Given
            when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));

            // When
            Optional<Person> result = personService.getPersonById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("tim@taler.de");
            verify(personRepository).findById(1L);
        }

        @Test
        @DisplayName("should return empty when person not found")
        void shouldReturnEmptyWhenPersonNotFound() {
            // Given
            when(personRepository.findById(99L)).thenReturn(Optional.empty());

            // When
            Optional<Person> result = personService.getPersonById(99L);

            // Then
            assertThat(result).isEmpty();
            verify(personRepository).findById(99L);
        }
    }

    @Nested
    @DisplayName("createPerson()")
    class CreatePerson {

        @Test
        @DisplayName("should save person and return saved entity")
        void shouldSavePersonAndReturnSavedEntity() {
            // Given
            when(personRepository.save(any(Person.class))).thenReturn(testPerson);

            // When
            Person result = personService.createPerson(testPerson);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("Tim");
            verify(personRepository).save(any(Person.class));
        }

        @Test
        @DisplayName("should send email after creating person")
        void shouldSendEmailAfterCreatingPerson() {
            // Given
            when(personRepository.save(any(Person.class))).thenReturn(testPerson);

            // When
            personService.createPerson(testPerson);

            // Then
            verify(emailService).sendPersonCreatedEmailAsync(
                    eq("tim@taler.de"),
                    eq("Tim"),
                    eq("Taler"),
                    eq("tim@taler.de")
            );
        }
    }

    @Nested
    @DisplayName("updatePerson()")
    class UpdatePerson {

        @Test
        @DisplayName("should update person fields")
        void shouldUpdatePersonFields() {
            // Given
            Person updatedDetails = new Person();
            updatedDetails.setFirstName("Hans");
            updatedDetails.setLastName("Schmidt");
            updatedDetails.setEmail("hans@example.com");

            when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
            when(personRepository.save(any(Person.class))).thenReturn(testPerson);

            // When
            Person result = personService.updatePerson(1L, updatedDetails);

            // Then
            assertThat(testPerson.getFirstName()).isEqualTo("Hans");
            assertThat(testPerson.getLastName()).isEqualTo("Schmidt");
            verify(personRepository).save(testPerson);
        }

        @Test
        @DisplayName("should throw exception when person not found")
        void shouldThrowExceptionWhenPersonNotFound() {
            // Given
            when(personRepository.findById(99L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> personService.updatePerson(99L, testPerson))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("deletePerson()")
    class DeletePerson {

        @Test
        @DisplayName("should call deleteById on repository")
        void shouldCallDeleteByIdOnRepository() {
            // When
            personService.deletePerson(1L);

            // Then
            verify(personRepository).deleteById(1L);
        }
    }
}