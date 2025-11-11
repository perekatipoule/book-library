package vova.group.id.LibraryBoot.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vova.group.id.LibraryBoot.dto.BookDTO;

@Component
public class BookValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return BookDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BookDTO bookDTO = (BookDTO) target;

        if (!errors.hasFieldErrors("year")) {
            int year = Integer.parseInt(bookDTO.getYear());
            int currentYear = java.time.Year.now().getValue();
            if (year < 1400 || year > currentYear) {
                errors.rejectValue("year", "", "Year must be between 1400 and " + currentYear);
            }
        }
    }
}
