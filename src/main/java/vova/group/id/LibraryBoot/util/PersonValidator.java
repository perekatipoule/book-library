package vova.group.id.LibraryBoot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vova.group.id.LibraryBoot.dto.PersonDTO;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.PeopleService;


@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PersonDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonDTO personDTO = (PersonDTO) target;

        // Check for email uniqueness
        Person personFromDB = peopleService.show(personDTO.getEmail());
        if (personFromDB != null && personFromDB.getId() != personDTO.getId()) {
            errors.rejectValue("email", "", "A user with this email already exists");
        }

        // Birth year validations
        if (!errors.hasFieldErrors("birthYear")) {
            int year = Integer.parseInt(personDTO.getBirthYear());
            int currentYear = java.time.Year.now().getValue();
            if (year < 1900 || year > currentYear) {
                errors.rejectValue("birthYear", "", "Year must be between 1900 and " + currentYear);
            }
        }
    }
}

