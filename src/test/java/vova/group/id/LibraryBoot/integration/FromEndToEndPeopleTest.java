package vova.group.id.LibraryBoot.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import vova.group.id.LibraryBoot.dto.PersonDTO;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.PeopleService;
import vova.group.id.LibraryBoot.utils.H2databaseInitTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SuppressWarnings("unchecked")
public class FromEndToEndPeopleTest extends H2databaseInitTest {
    private final MockMvc mockMvc;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    @Autowired
    private PeopleService peopleService;

    @Autowired
    public FromEndToEndPeopleTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void indexTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/people"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("people"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/index", modelAndView.getViewName());

        List<Person> receivedPeople = (List<Person>) modelAndView.getModel().get("people");
        assertEquals(3, receivedPeople.size());
    }

    @Test
    public void testShowPersonWithoutBooks() throws Exception {
        mvcResult = mockMvc.perform(get("/library/people/{id}", 3))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("person", "personBooks"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/show", modelAndView.getViewName());

        Person receivedPerson = (Person) modelAndView.getModel().get("person");
        assertNotNull(receivedPerson);
        assertEquals("Test Name3", receivedPerson.getFullName());
        assertEquals(1999, receivedPerson.getBirthYear());
        assertEquals("test3@gmail.com", receivedPerson.getEmail());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("personBooks");
        assertNotNull(receivedBooks);
        assertEquals(0, receivedBooks.size());
    }

    @Test
    public void testShowPersonWithBooks() throws Exception {
        mvcResult = mockMvc.perform(get("/library/people/{id}", 1))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("person", "personBooks"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/show", modelAndView.getViewName());

        Person receivedPerson = (Person) modelAndView.getModel().get("person");
        assertNotNull(receivedPerson);
        assertEquals("Test Name1", receivedPerson.getFullName());
        assertEquals(1970, receivedPerson.getBirthYear());
        assertEquals("test1@gmail.com", receivedPerson.getEmail());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("personBooks");
        assertNotNull(receivedBooks);
        assertEquals(1, receivedBooks.size());
        assertEquals("Test Title2", receivedBooks.getFirst().getTitle());
    }

    @Test
    public void testCreateWithNotUniqueEmail() throws Exception {
        List<Person> receivedPeopleInitial = peopleService.index();
        assertEquals(3, receivedPeopleInitial.size());

        PersonDTO testPersonDTO = new PersonDTO(4, "Test Name", "1980", "test1@gmail.com");
        mvcResult = mockMvc.perform(post("/library/people")
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("personDTO", testPersonDTO),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/new", modelAndView.getViewName());

        List<Person> receivedPeopleAfterCreate = peopleService.index();
        assertEquals(3, receivedPeopleAfterCreate.size());
    }

    @Test
    public void testCreateWithCorrectPerson() throws Exception {
        List<Person> receivedPeopleInitial = peopleService.index();
        assertEquals(3, receivedPeopleInitial.size());

        PersonDTO testPersonDTO = new PersonDTO("Test Name", "1980", "test4@gmail.com");
        mockMvc.perform(post("/library/people")
                        .flashAttr("personDTO", testPersonDTO))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people"));

        List<Person> receivedPeopleAfterCreate = peopleService.index();
        assertEquals(4, receivedPeopleAfterCreate.size());
        assertEquals("test4@gmail.com", receivedPeopleAfterCreate.get(3).getEmail());
    }

    @Test
    public void testEdit() throws Exception {
        mvcResult = mockMvc.perform(get("/library/people/{id}/edit", 1))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("personDTO"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/edit", modelAndView.getViewName());

        PersonDTO receivedPersonDTO = (PersonDTO) modelAndView.getModel().get("personDTO");
        assertNotNull(receivedPersonDTO);
        assertEquals("Test Name1", receivedPersonDTO.getFullName());
        assertEquals("1970", receivedPersonDTO.getBirthYear());
        assertEquals("test1@gmail.com", receivedPersonDTO.getEmail());
    }

    @Test
    public void testUpdateWithNotUniqueEmail() throws Exception {
        PersonDTO firstPersonDTO = new PersonDTO("Test Name1", "1970", "test3@gmail.com");
        mvcResult = mockMvc.perform(patch("/library/people/{id}", 1)
                        .flashAttr("personDTO", firstPersonDTO))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("personDTO"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/edit", modelAndView.getViewName());

        Person firstPerson = peopleService.show(1);
        assertNotNull(firstPerson);
        assertEquals("test1@gmail.com", firstPerson.getEmail());
    }

    @Test
    public void testUpdateWithSameData() throws Exception {
        PersonDTO firstPersonDTO = new PersonDTO("Test Name1", "1970", "test1@gmail.com");
        mockMvc.perform(patch("/library/people/{id}", 1)
                        .flashAttr("personDTO", firstPersonDTO))
                .andExpect(status().isOk())
                .andReturn();

        Person firstPersonFromDB = peopleService.show(1);
        assertNotNull(firstPersonFromDB);
        assertEquals("Test Name1", firstPersonFromDB.getFullName());
        assertEquals(1970, firstPersonFromDB.getBirthYear());
        assertEquals("test1@gmail.com", firstPersonFromDB.getEmail());
    }

    @Test
    public void testUpdateWithNewValidDate() throws Exception {
        PersonDTO firstPersonDTO = new PersonDTO("Another Name", "1971", "another@gmail.com");
        mockMvc.perform(patch("/library/people/{id}", 1)
                        .flashAttr("personDTO", firstPersonDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/people/1"))
                .andReturn();

        Person firstPersonFromDB = peopleService.show(1);
        assertNotNull(firstPersonFromDB);
        assertEquals("Another Name", firstPersonFromDB.getFullName());
        assertEquals(1971, firstPersonFromDB.getBirthYear());
        assertEquals("another@gmail.com", firstPersonFromDB.getEmail());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/library/people/{id}", 1))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people")
                );
    }
}
