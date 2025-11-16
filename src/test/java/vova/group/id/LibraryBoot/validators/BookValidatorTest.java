package vova.group.id.LibraryBoot.validators;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import vova.group.id.LibraryBoot.dto.BookDTO;
import vova.group.id.LibraryBoot.dto.PersonDTO;
import vova.group.id.LibraryBoot.util.BookValidator;
import vova.group.id.LibraryBoot.utils.H2databaseInitTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class BookValidatorTest extends H2databaseInitTest {
    @Autowired
    private BookValidator validator;

    @Test
    public void supportsTest() {
        assertTrue(validator.supports(BookDTO.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    public void testValidateWithValidPerson() {
        // given
        BookDTO bookDTO = new BookDTO();
        bookDTO.setYear("2015");
        Errors errors = new BeanPropertyBindingResult(bookDTO, "book");
        // when
        validator.validate(bookDTO, errors);
        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidateWithYearEarlierThan1400() {
        // given
        BookDTO bookDTO = new BookDTO();
        bookDTO.setYear("1399");
        Errors errors = new BeanPropertyBindingResult(bookDTO, "book");
        //when
        validator.validate(bookDTO, errors);
        // then
        assertTrue(errors.hasErrors());
        assertTrue(errors.getErrorCount() == 1);
        assertTrue(errors.hasFieldErrors("year"));
        assertEquals("Year must be between 1400 and " + java.time.Year.now().getValue(), Objects.requireNonNull(errors.getFieldError("year")).getDefaultMessage());
    }

    @Test
    public void testValidateWithYearGreaterThan1400() {
        // given
        BookDTO bookDTO = new BookDTO();
        String yearGraterThanNow = String.valueOf(java.time.Year.now().getValue() + 1);
        bookDTO.setYear(yearGraterThanNow);
        Errors errors = new BeanPropertyBindingResult(bookDTO, "book");
        //when
        validator.validate(bookDTO, errors);
        // then
        assertTrue(errors.hasErrors());
        assertTrue(errors.getErrorCount() == 1);
        assertTrue(errors.hasFieldErrors("year"));
        assertEquals("Year must be between 1400 and " + java.time.Year.now().getValue(), Objects.requireNonNull(errors.getFieldError("year")).getDefaultMessage());
    }
}
