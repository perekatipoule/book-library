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
import vova.group.id.LibraryBoot.dto.BookDTO;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.BookPageForm;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.BooksService;
import vova.group.id.LibraryBoot.services.PeopleService;
import vova.group.id.LibraryBoot.util.BookValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BooksControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BooksService booksService;

    @Mock
    private PeopleService peopleService;

    @Autowired
    private BookValidator bookValidator;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BooksController booksController;

    private Book testBook;
    private BookDTO testBookDTO;
    private List<Book> testBooks;
    private Person testPerson;

    @BeforeEach
    public void setup() {
        testBook = new Book();
        testBook.setId(new Random().nextInt(300));
        testBookDTO = new BookDTO();
        testBookDTO.setId(testBook.getId());
        testBooks = new ArrayList<>();
        testPerson = new Person();
        testPerson.setId(new Random().nextInt(300));
        mockMvc = MockMvcBuilders.standaloneSetup(new BooksController(booksService, peopleService, bookValidator, modelMapper)).build();
    }

    @Test
    public void testIndex() throws Exception {
        when(booksService.index(any(BookPageForm.class))).thenReturn(testBooks);

        // test with not valid form parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books")
                        .param("page", "page")
                        .param("booksPerPage", "-1")
                        .param("sortByYear", "false"))
                .andExpectAll(
                        status().isOk(),
                        model().attribute("form", instanceOf(BookPageForm.class)),
                        forwardedUrl("books/index")
                );

        // test with valid form parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books")
                        .param("page", "1")
                        .param("booksPerPage", "2")
                        .param("sortByYear", "false"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("books", testBooks),
                        model().attribute("form", instanceOf(BookPageForm.class)),
                        status().isOk(),
                        forwardedUrl("books/index")
                );

        // test with empty form
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books"))
                .andExpectAll(
                        model().size(2),
                        model().attribute("books", testBooks),
                        model().attribute("form", instanceOf(BookPageForm.class)),
                        status().isOk(),
                        forwardedUrl("books/index")
                );

        verify(booksService, times(2)).index(any(BookPageForm.class));
    }

    @Test
    public void testShow() throws Exception {
        // Test show book without reader
        when(booksService.show(testBook.getId())).thenReturn(testBook);
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books/{id}", testBook.getId()))
                .andExpectAll(
                        model().size(3),
                        model().attributeExists("person", "book", "people"),
                        status().isOk(),
                        forwardedUrl("books/show")
                );

        // Test show book with reader
        when(booksService.showBookReader(testBook.getId())).thenReturn(testPerson);
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books/{id}", testBook.getId()))
                .andExpectAll(
                        model().size(3),
                        model().attributeExists("person", "book", "bookReader"),
                        status().isOk(),
                        forwardedUrl("books/show")
                );

        verify(booksService, times(2)).show(testBook.getId());
        verify(booksService, times(2)).showBookReader(testBook.getId());
        verify(peopleService, times(1)).index();
    }

    @Test
    public void testSearch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books/search"))
                .andExpectAll(
                        status().isOk(),
                        forwardedUrl("books/search")
                );
    }

    @Test
    public void testFindBook() throws Exception {
         when(booksService.index(anyString())).thenReturn(testBooks);

        mockMvc.perform(post("/library/books/search")
                        .param("titleFragment", anyString()))
                .andExpectAll(
                        model().size(1),
                        model().attribute("searchedBooks", testBooks),
                        status().isOk(),
                        forwardedUrl("books/search")
                );

        verify(booksService, times(1)).index(anyString());
    }

    @Test
    public void testNewBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books/new"))
                .andExpectAll(
                        status().isOk(),
                        model().size(1),
                        model().attributeExists("bookDTO"),
                        forwardedUrl("books/new")
                );
    }

    @Test
    public void testCreateBook() throws Exception {
        // test an empty new book
        mockMvc.perform(post("/library/books")
                        .flashAttr("bookDTO", testBookDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("bookDTO", testBookDTO),
                        model().attributeErrorCount("bookDTO", 3),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a book with not valid fields
        testBookDTO.setTitle("a");
        testBookDTO.setAuthor("b");
        testBookDTO.setYear("1300");
        mockMvc.perform(post("/library/books")
                        .flashAttr("bookDTO", testBookDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("bookDTO", testBookDTO),
                        model().attributeErrorCount("bookDTO", 3),
                        status().isOk(),
                        forwardedUrl("books/new")
                );

        // test a book with valid fields
        testBookDTO.setTitle("test title");
        testBookDTO.setAuthor("test author");
        testBookDTO.setYear("1600");
        mockMvc.perform(post("/library/books")
                        .flashAttr("bookDTO", testBookDTO))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books")
                );

        verify(booksService, times(1)).save(any(Book.class));
    }

    @Test
    public void testEditBook() throws Exception {
        when(booksService.show(testBook.getId())).thenReturn(testBook);
        BookDTO bookDTO = modelMapper.map(testBook, BookDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/library/books/{id}/edit", testBook.getId()))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("bookDTO"),
                        model().attribute("bookDTO", org.hamcrest.Matchers.samePropertyValuesAs(bookDTO)),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );
        verify(booksService, times(1)).show(testBook.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        // test an empty new person
        mockMvc.perform(patch("/library/books/{id}", testBookDTO.getId())
                        .flashAttr("bookDTO", testBookDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("bookDTO", testBookDTO),
                        model().attributeErrorCount("bookDTO", 3),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );

        // test a person with not valid fields
        testBookDTO.setTitle("a");
        testBookDTO.setAuthor("b");
        testBookDTO.setYear("1300");
        mockMvc.perform(patch("/library/books/{id}", testBookDTO.getId())
                        .flashAttr("bookDTO", testBookDTO))
                .andExpectAll(
                        model().size(1),
                        model().attribute("bookDTO", testBookDTO),
                        model().attributeErrorCount("bookDTO", 3),
                        status().isOk(),
                        forwardedUrl("books/edit")
                );

        // test a person with valid fields
        testBookDTO.setTitle("test title");
        testBookDTO.setAuthor("test author");
        testBookDTO.setYear("1600");
        mockMvc.perform(patch("/library/books/{id}", testBookDTO.getId())
                        .flashAttr("bookDTO", testBookDTO))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + testBookDTO.getId())
                );

        Book book = booksController.convertBookDTOToBook(testBookDTO);
        verify(booksService, times(1)).update(eq(testBookDTO.getId()), refEq(book));
    }

    @Test
    public void testDelete() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.delete("/library/books/{id}", testBook.getId()))
                    .andExpectAll(
                            status().is3xxRedirection(),
                            redirectedUrl("/library/books")
                    );
        }
        verify(booksService, times(3)).delete(testBook.getId());
    }

    @Test
    public void testAppointPerson() throws Exception {
        mockMvc.perform(patch("/library/books/{id}/appoint", testBook.getId())
                        .flashAttr("person", testPerson))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + testBook.getId())
                );
        verify(booksService, times(1)).appointPerson(testPerson, testBook.getId());
    }

    @Test
    public void testFreeBook() throws Exception {
        mockMvc.perform(patch("/library/books/{id}/free", testBook.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/books/" + testBook.getId())
                );
        verify(booksService, times(1)).free(testBook.getId());
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(peopleService);
        verifyNoMoreInteractions(booksService);
    }
}
