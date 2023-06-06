package vova.group.id.LibraryBoot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
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
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;

        // Check for email uniqueness
        Person personFromDB = peopleService.show(person.getEmail());
        if (personFromDB != null && personFromDB.getId() != person.getId()) {
            errors.rejectValue("email", "", "A user with this email already exists");
        }
    }
}

