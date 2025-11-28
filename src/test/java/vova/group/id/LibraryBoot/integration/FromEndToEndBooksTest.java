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
import vova.group.id.LibraryBoot.dto.BookDTO;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.BookPageForm;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.BooksService;
import vova.group.id.LibraryBoot.services.PeopleService;
import vova.group.id.LibraryBoot.utils.H2databaseInitTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SuppressWarnings("unchecked")
public class FromEndToEndBooksTest extends H2databaseInitTest {
    private final MockMvc mockMvc;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    @Autowired
    private BooksService booksService;

    @Autowired
    private PeopleService peopleService;

    @Autowired
    public FromEndToEndBooksTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testIndexWithEmptyForm() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .flashAttr("form", new BookPageForm()))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("books", "form"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/index", modelAndView.getViewName());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertNotNull(receivedBooks);
        assertEquals(2, receivedBooks.size());
    }

    @Test
    public void testIndexWithPaginationOnly() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .flashAttr("form", new BookPageForm("1", "1", null)))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("books", "form"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/index", modelAndView.getViewName());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertNotNull(receivedBooks);
        assertEquals(1, receivedBooks.size());
        assertEquals("Test Title2", receivedBooks.getFirst().getTitle());
    }

    @Test
    public void testIndexWithSortingOnly() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .flashAttr("form", new BookPageForm(null, null, true)))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("books", "form"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/index", modelAndView.getViewName());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertNotNull(receivedBooks);
        assertEquals(2, receivedBooks.size());
        assertEquals("Test Title2", receivedBooks.get(0).getTitle());
        assertEquals("Test Title1", receivedBooks.get(1).getTitle());
    }

    @Test
    public void testIndexWithSortingAndPagination() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books")
                        .flashAttr("form", new BookPageForm("0","2", true)))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("books", "form"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/index", modelAndView.getViewName());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertNotNull(receivedBooks);
        assertEquals(2, receivedBooks.size());
        assertEquals("Test Title2", receivedBooks.get(0).getTitle());
        assertEquals("Test Title1", receivedBooks.get(1).getTitle());
    }

    @Test
    public void testShowBookWithoutReader() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books/{id}", 1))
                .andExpectAll(
                        model().size(3),
                        model().attributeExists("book", "people", "person"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        List<Person> receivedPeople = (List<Person>) modelAndView.getModel().get("people");
        assertNotNull(receivedPeople);
        assertEquals(3, receivedPeople.size());

        Book receivedBook = (Book) modelAndView.getModel().get("book");
        assertNotNull(receivedBook);
        assertEquals("Test Title1", receivedBook.getTitle());
    }

    @Test
    public void testShowBookWithReader() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books/{id}", 2))
                .andExpectAll(
                        model().size(3),
                        model().attributeExists("book", "bookReader", "person"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/show", modelAndView.getViewName());

        Person receivedPerson = (Person) modelAndView.getModel().get("bookReader");
        assertNotNull(receivedPerson);
        assertEquals("Test Name1", receivedPerson.getFullName());
        assertEquals("test1@gmail.com", receivedPerson.getEmail());

        Book receivedBook = (Book) modelAndView.getModel().get("book");
        assertNotNull(receivedBook);
        assertEquals("Test Title2", receivedBook.getTitle());
    }

    @Test
    public void testFindNotExistingBook() throws Exception {
        mvcResult = mockMvc.perform(post("/library/books/search")
                .param("titleFragment", "not existing book fragment"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("searchedBooks"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/search", modelAndView.getViewName());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("searchedBooks");
        assertNotNull(receivedBooks);
        assertTrue(receivedBooks.isEmpty());
    }

    @Test
    public void testFindExistingBook() throws Exception {
        mvcResult = mockMvc.perform(post("/library/books/search")
                        .param("titleFragment", "Test"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("searchedBooks"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/search", modelAndView.getViewName());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("searchedBooks");
        assertEquals(2, receivedBooks.size());
        assertEquals("Test Title1", receivedBooks.get(0).getTitle());
        assertEquals("Test Title2", receivedBooks.get(1).getTitle());
    }

    @Test
    public void testCreateWithNotValidYear() throws Exception {
        BookPageForm bookPageForm = new BookPageForm();
        List<Book> receivedBooksInitial = booksService.index(bookPageForm);
        assertEquals(2, receivedBooksInitial.size());

        BookDTO bookDTO = new BookDTO( "Not Valid Book", "Test Author", "1300");
        mvcResult = mockMvc.perform(post("/library/books")
                        .flashAttr("bookDTO", bookDTO))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("bookDTO"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/new", modelAndView.getViewName());

        List<Book> receivedBooksAfterCreate = booksService.index(bookPageForm);
        assertEquals(2, receivedBooksAfterCreate.size());
    }

    @Test
    public void testCreateWithCorrectPerson() throws Exception {
        BookPageForm bookPageForm = new BookPageForm();
        List<Book> receivedBooksInitial = booksService.index(bookPageForm);
        assertEquals(2, receivedBooksInitial.size());

        BookDTO bookDTO = new BookDTO( "Valid Book", "Test Author", "1700");
        mvcResult = mockMvc.perform(post("/library/books")
                        .flashAttr("bookDTO", bookDTO))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("redirect:/library/books", modelAndView.getViewName());

        List<Book> receivedBooksAfterCreate = booksService.index(bookPageForm);
        assertEquals(3, receivedBooksAfterCreate.size());
        assertEquals("Valid Book", receivedBooksAfterCreate.get(2).getTitle());
        assertEquals("Test Author", receivedBooksAfterCreate.get(2).getAuthor());
    }

    @Test
    public void testEdit() throws Exception {
        mvcResult = mockMvc.perform(get("/library/books/{id}/edit", 1))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("bookDTO"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/edit", modelAndView.getViewName());

        BookDTO receivedBookDTO = (BookDTO) modelAndView.getModel().get("bookDTO");
        assertNotNull(receivedBookDTO);
        assertEquals("Test Title1", receivedBookDTO.getTitle());
        assertEquals("1946", receivedBookDTO.getYear());
        assertEquals("Ivan Bagryany", receivedBookDTO.getAuthor());
    }

    @Test
    public void testUpdateWithSameData() throws Exception {
       mockMvc.perform(patch("/library/books/{id}", 1)
                       .flashAttr("bookDTO", new BookDTO("Test Title1", "Ivan Bagryany", "1946")))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Book firstBookFromDB = booksService.show(1);
        assertNotNull(firstBookFromDB);
        assertEquals("Test Title1", firstBookFromDB.getTitle());
        assertEquals(1946, firstBookFromDB.getYear());
        assertEquals("Ivan Bagryany", firstBookFromDB.getAuthor());
    }

    @Test
    public void testUpdateWithValidData() throws Exception {
        mockMvc.perform(patch("/library/books/{id}", 1)
                        .flashAttr("bookDTO", new BookDTO("Updated Title", "Updated Author", "1900")))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Book firstBookFromDB = booksService.show(1);
        assertNotNull(firstBookFromDB);
        assertEquals("Updated Title", firstBookFromDB.getTitle());
        assertEquals(1900, firstBookFromDB.getYear());
        assertEquals("Updated Author", firstBookFromDB.getAuthor());
    }

    @Test
    public void testUpdateWithNotValidData() throws Exception {
       mvcResult =  mockMvc.perform(patch("/library/books/{id}", 1)
                        .flashAttr("bookDTO", new BookDTO("Updated Title", "Updated Author", "1300")))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("bookDTO"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("books/edit", modelAndView.getViewName());

        Book firstBookFromDB = booksService.show(1);
        assertNotNull(firstBookFromDB);
        assertEquals("Test Title1", firstBookFromDB.getTitle());
        assertEquals(1946, firstBookFromDB.getYear());
        assertEquals("Ivan Bagryany", firstBookFromDB.getAuthor());
    }

    @Test
    public void testAppointPerson() throws Exception {
        Book bookForAdding = booksService.show(1);
        assertNull(bookForAdding.getReader());
        assertNull(bookForAdding.getTakenAt());

        Person personToAppoint = peopleService.show(2);

        mockMvc.perform(patch("/library/books/{id}/appoint", bookForAdding.getId())
                        .flashAttr("person", personToAppoint))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + bookForAdding.getId()));

        bookForAdding = booksService.show(1);
        assertNotNull(bookForAdding.getReader());
        assertNotNull(bookForAdding.getTakenAt());
    }

    @Test
    public void testFree() throws Exception {
        Book bookWithReader = booksService.show(2);
        assertNotNull(bookWithReader.getReader());
        assertNotNull(bookWithReader.getTakenAt());


        mockMvc.perform(patch("/library/books/{id}/free", bookWithReader.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + bookWithReader.getId()));

        Book bookWithoutReader = booksService.show(2);
        assertNull(bookWithoutReader.getReader());
        assertNull(bookWithoutReader.getTakenAt());
    }

    @Test
    public void testDelete() throws Exception {
        List<Book> booksBeforeDelete = booksService.index(new BookPageForm());
        assertEquals(2, booksBeforeDelete.size());

        mockMvc.perform(delete("/library/books/{id}", 2))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books")
                );

        Book delatedBook = booksService.show(2);
        assertNull(delatedBook);
        List<Book> booksAfterDelete = booksService.index(new BookPageForm());
        assertEquals(1, booksAfterDelete.size());
    }
}
