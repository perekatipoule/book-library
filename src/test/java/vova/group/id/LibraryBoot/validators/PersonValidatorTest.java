package vova.group.id.LibraryBoot.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import vova.group.id.LibraryBoot.dto.PersonDTO;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.repositories.BooksRepository;
import vova.group.id.LibraryBoot.repositories.PeopleRepository;
import vova.group.id.LibraryBoot.services.PeopleService;
import vova.group.id.LibraryBoot.util.PersonValidator;
import vova.group.id.LibraryBoot.utils.H2databaseInitTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class PersonValidatorTest extends H2databaseInitTest {
    @Autowired
    private PersonValidator validator;

    @Test
    public void supportsTest() {
        assertTrue(validator.supports(PersonDTO.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    public void testValidateWithValidPerson() {
        // given
        PersonDTO testPersonDTO = new PersonDTO(4, "Test Name", "1999", "any@ukr.net");
        Errors errors = new BeanPropertyBindingResult(testPersonDTO, "person");
        // when
        validator.validate(testPersonDTO, errors);
        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testPersonWithNotUniqueEmail() {
        // given
        PersonDTO testPersonDTO = new PersonDTO(4, "Test Name", "1999", "test@ukr.net");
        Errors errors = new BeanPropertyBindingResult(testPersonDTO, "person");
        // when
        validator.validate(testPersonDTO, errors);
        // then
        assertTrue(errors.hasErrors());
        assertTrue(errors.getErrorCount() == 1);
        assertTrue(errors.hasFieldErrors("email"));
        assertEquals("A user with this email already exists", Objects.requireNonNull(errors.getFieldError("email")).getDefaultMessage());
    }

    @Test
    public void testPersonWithYearEarlierThan1900() {
        // given
        PersonDTO testPersonDTO = new PersonDTO(4, "Test Name", "1800", "any@ukr.net");
        Errors errors = new BeanPropertyBindingResult(testPersonDTO, "person");
        // when
        validator.validate(testPersonDTO, errors);
        // then
        assertTrue(errors.hasErrors());
        assertTrue(errors.getErrorCount() == 1);
        assertTrue(errors.hasFieldErrors("birthYear"));
        assertEquals("Year must be between 1900 and " + java.time.Year.now().getValue(), Objects.requireNonNull(errors.getFieldError("birthYear")).getDefaultMessage());
    }

    @Test
    public void testPersonWithYearGraterThannow() {
        // given
        String yearGraterThanNow = String.valueOf(java.time.Year.now().getValue() + 1);
        PersonDTO testPersonDTO = new PersonDTO(4, "Test Name", yearGraterThanNow, "any@ukr.net");
        Errors errors = new BeanPropertyBindingResult(testPersonDTO, "person");
        // when
        validator.validate(testPersonDTO, errors);
        // then
        assertTrue(errors.hasErrors());
        assertTrue(errors.getErrorCount() == 1);
        assertTrue(errors.hasFieldErrors("birthYear"));
        assertEquals("Year must be between 1900 and " + java.time.Year.now().getValue(), Objects.requireNonNull(errors.getFieldError("birthYear")).getDefaultMessage());
    }

    @Test
    public void testPersonWithYearAndEmailErrors() {
        // given
        PersonDTO testPersonDTO = new PersonDTO(4, "Test Name", "1800", "test@ukr.net");
        Errors errors = new BeanPropertyBindingResult(testPersonDTO, "person");
        // when
        validator.validate(testPersonDTO, errors);
        // then
        assertTrue(errors.hasErrors());
        assertTrue(errors.getErrorCount() == 2);
        assertTrue(errors.hasFieldErrors("birthYear"));
        assertTrue(errors.hasFieldErrors("email"));
        assertEquals("Year must be between 1900 and " + java.time.Year.now().getValue(), Objects.requireNonNull(errors.getFieldError("birthYear")).getDefaultMessage());
        assertEquals("A user with this email already exists", Objects.requireNonNull(errors.getFieldError("email")).getDefaultMessage());
    }
}
