package vova.group.id.LibraryBoot.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vova.group.id.LibraryBoot.dto.PersonDTO;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.PeopleService;
import vova.group.id.LibraryBoot.util.PersonValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class PeopleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PeopleService peopleService;

    @Autowired
    private PersonValidator personValidator;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PeopleController peopleController;

    private Person testPerson;
    private PersonDTO testPersonDTO;
    private List<Person> testPeople;
    private List<Book> testBooks;

    @BeforeEach
    public void setup() {
        testPerson = new Person();
        testPerson.setId(new Random().nextInt(300));
        testPeople = new ArrayList<>();
        testPersonDTO = new PersonDTO();
        testPersonDTO.setId(testPerson.getId());
        testBooks = new ArrayList<>();
        mockMvc = MockMvcBuilders.standaloneSetup(new PeopleController(peopleService, personValidator, modelMapper)).build();
    }

    @Test
    public void testIndex() throws Exception {
        when(peopleService.index()).thenReturn(testPeople);
        mockMvc.perform(MockMvcRequestBuilders.get("/library/people"))
                .andExpectAll(
                        model().size(1),
                        model().attribute("people", testPeople),
                        status().isOk(),
                        forwardedUrl("people/index")
                );
        verify(peopleService, times(1)).index();
    }

    @Test
    public void testShow() throws Exception {
        when(peopleService.show(anyInt())).thenReturn(testPerson);
        when(peopleService.showPersonBooks(anyInt())).thenReturn(testBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/library/people/{id}", anyInt()))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("person", "personBooks"),
                        status().isOk(),
                        forwardedUrl("people/show")
                );

        verify(peopleService, times(1)).show(anyInt());
        verify(peopleService, times(1)).showPersonBooks(anyInt());
    }

    @Test
    public void newPersonTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/library/people/new"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("personDTO"),
                        status().isOk(),
                        forwardedUrl("people/new")
                );
    }

    @Test
    public void createTest() throws Exception {
        // test an empty new person
        mockMvc.perform(post("/library/people")
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("personDTO", testPersonDTO),
                        model().attributeErrorCount("personDTO", 3),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with not valid fields
        testPersonDTO.setFullName("Name");
        testPersonDTO.setBirthYear("1600");
        testPersonDTO.setEmail("test");
        mockMvc.perform(post("/library/people")
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("personDTO", testPersonDTO),
                        model().attributeErrorCount("personDTO", 3),
                        status().isOk(),
                        forwardedUrl("people/new")
                );

        // test a person with valid fields
        testPersonDTO.setFullName("Test Name");
        testPersonDTO.setBirthYear("1990");
        testPersonDTO.setEmail("test@gmail.com");
        mockMvc.perform(post("/library/people")
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people")
                );

        verify(peopleService, times(1)).save(any(Person.class));
    }

    @Test
    public void editTest() throws Exception {
        when(peopleService.show(anyInt())).thenReturn(testPerson);
        PersonDTO expectedDTO = peopleController.convertPersonToPersonDTO(testPerson);

        mockMvc.perform(MockMvcRequestBuilders.get("/library/people/{id}/edit", testPerson.getId()))
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("personDTO"),
                        model().attribute("personDTO",
                                org.hamcrest.Matchers.samePropertyValuesAs(expectedDTO)
                        ),
                        forwardedUrl("people/edit")
                );

        verify(peopleService, times(1)).show(testPerson.getId());
    }

    @Test
    public void updateTest() throws Exception {
        // test an empty new person
        mockMvc.perform(patch("/library/people/{id}", testPersonDTO.getId())
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("personDTO", testPersonDTO),
                        model().attributeErrorCount("personDTO", 3),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );

        // test a person with not valid fields
        testPersonDTO.setFullName("Name");
        testPersonDTO.setBirthYear("1600");
        testPersonDTO.setEmail("test");
        mockMvc.perform(patch("/library/people/{id}", testPersonDTO.getId())
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("personDTO", testPersonDTO),
                        model().attributeErrorCount("personDTO", 3),
                        status().isOk(),
                        forwardedUrl("people/edit")
                );

        // test a person with valid fields
        testPersonDTO.setFullName("Test Name");
        testPersonDTO.setBirthYear("1990");
        testPersonDTO.setEmail("test@gmail.com");
        mockMvc.perform(patch("/library/people/{id}", testPersonDTO.getId())
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people/" + testPersonDTO.getId())
                );


        Person personToBeAddedToDB = peopleController.convertPersonDTOToPerson(testPersonDTO);
        verify(peopleService, times(1)).update(eq(testPersonDTO.getId()), refEq(personToBeAddedToDB));
    }

    @Test
    public void deleteTest() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.delete("/library/people/{id}", testPerson.getId()))
                    .andExpectAll(
                            status().is3xxRedirection(),
                            redirectedUrl("/library/people")
                    );
        }
        verify(peopleService, times(3)).delete(testPerson.getId());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(peopleService);
    }
}
