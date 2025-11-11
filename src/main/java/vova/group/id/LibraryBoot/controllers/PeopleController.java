package vova.group.id.LibraryBoot.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vova.group.id.LibraryBoot.dto.PersonDTO;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.PeopleService;
import vova.group.id.LibraryBoot.util.PersonValidator;


@Controller
@RequestMapping("/library/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.index());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.show(id));
        model.addAttribute("personBooks", peopleService.showPersonBooks(id));

        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("personDTO") PersonDTO personDTO) {
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("personDTO") @Valid PersonDTO personDTO,
                         BindingResult bindingResult) {

        personValidator.validate(personDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        peopleService.save(convertPersonDTOToPerson(personDTO));
        return "redirect:/library/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        Person personFromDB = peopleService.show(id);
        model.addAttribute("personDTO", convertPersonToPersonDTO(personFromDB));
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("personDTO") @Valid PersonDTO personDTO, BindingResult bindingResult, @PathVariable("id") int id) {

        personValidator.validate(personDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/edit";
        }

        peopleService.update(id, convertPersonDTOToPerson(personDTO));
        return "redirect:/library/people/" + id;
    }

    public Person convertPersonDTOToPerson(PersonDTO personDTO) {
        Person person = modelMapper.map(personDTO, Person.class);
        person.setBirthYear(Integer.parseInt(personDTO.getBirthYear()));
        return person;
    }

    public PersonDTO convertPersonToPersonDTO(Person person) {
        PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);
        personDTO.setBirthYear(String.valueOf(person.getBirthYear()));
        return personDTO;
    }

    @DeleteMapping(("/{id}"))
    public String delate(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/library/people";
    }
}
