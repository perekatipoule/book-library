package vova.group.id.LibraryBoot.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.BookPageForm;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.repositories.BooksRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
public class BooksServiceTest {
    @MockitoBean
    private BooksRepository booksRepository;

    @Autowired
    private BooksService booksService;

    private Book testBook;
    private List<Book> testBooks;
    private Person testPerson;
    private final static int TEST_ID = 77;

    @BeforeEach
    public void setUp() {
        testBook = new Book();
        testBooks = Collections.singletonList(testBook);
        testPerson = new Person();
    }

    @Test
    public void testIndexByFormWithEmptyForm() {
        // given
        BookPageForm form = new BookPageForm();
        when(booksRepository.findAll()).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = booksService.index(form);
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository).findAll();
    }

    @Test
    public void testIndexByFormWithPaginationAndSorting() {
        // given
        BookPageForm form = new BookPageForm("2","3",true);
        Page<Book> page = new PageImpl<>(testBooks);
        when(booksRepository.findAll(PageRequest.of(2, 3, Sort.by("year")))).thenReturn(page);
        // when
        List<Book> receivedBooks = booksService.index(form);
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository).findAll(PageRequest.of(2, 3, Sort.by("year")));
    }

    @Test
    public void testIndexByFormWithPaginationOnly() {
        // given
        BookPageForm form = new BookPageForm("2","3",false);
        Page<Book> page = new PageImpl<>(testBooks);
        when(booksRepository.findAll(PageRequest.of(2, 3))).thenReturn(page);
        // when
        List<Book> receivedBooks = booksService.index(form);
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository).findAll(PageRequest.of(2, 3));
    }

    @Test
    public void testIndexByFormWithSortingOnly() {
        // given
        BookPageForm form = new BookPageForm(null,null,true);
        when(booksRepository.findAll(Sort.by("year"))).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = booksService.index(form);
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository).findAll(Sort.by("year"));
    }

    @Test
    public void testIndexByTitleFragment() {
        // given
        when(booksRepository.findByTitleContainingIgnoreCase(anyString())).thenReturn(testBooks);
        // when
        List<Book> receivedBooks = booksService.index(anyString());
        // then
        assertIterableEquals(testBooks, receivedBooks);
        verify(booksRepository).findByTitleContainingIgnoreCase(anyString());
    }

    @Test
    public void tesShow() {
        // given
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        Book receivedBook = booksService.show(anyInt());
        // then
        assertEquals(testBook, receivedBook);
        verify(booksRepository).findById(anyInt());
    }

    @Test
    public void testShowBookWithEmptyReader() {
        // given
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        Person receivedPerson = booksService.showBookReader(anyInt());
        // then
        assertNull(receivedPerson);
        verify(booksRepository).findById(anyInt());
    }

    @Test
    public void testShowBookWithReaderExist() {
        // given
        testBook.setReader(testPerson);
        when(booksRepository.findById(anyInt())).thenReturn(Optional.of(testBook));
        // when
        Person receivedPerson = booksService.showBookReader(anyInt());
        // then
        assertEquals(testPerson, receivedPerson);
        verify(booksRepository).findById(anyInt());
    }

    @Test
    public void testSave() {
        // given, when
        for (int i = 0; i < 5; i++) booksService.save(new Book());
        // then
        verify(booksRepository, times(5)).save(any(Book.class));
    }

    @Test
    public void testUpdate() {
        // given
        Book updatedBook = new Book();
        testBook.setReader(testPerson);
        when(booksRepository.findById(TEST_ID)).thenReturn(Optional.of(testBook));
        // when
        booksService.update(TEST_ID, updatedBook);
        // then
        assertEquals(TEST_ID, updatedBook.getId());
        assertEquals(testPerson, updatedBook.getReader());
        verify(booksRepository).findById(TEST_ID);
        verify(booksRepository).save(updatedBook);
    }

    @Test
    public void testAppointPersonWithBookExist() {
        // given
        when(booksRepository.findById(TEST_ID)).thenReturn(Optional.of(testBook));
        // when
        booksService.appointPerson(testPerson, TEST_ID);
        // then
        assertEquals(testPerson, testBook.getReader());
        Date now = new Date();
        long diff = Math.abs(now.getTime() - testBook.getTakenAt().getTime());
        assertTrue(diff < 1000, "takenAt should be set to current time");
        verify(booksRepository).findById(TEST_ID);
    }

    @Test
    public void testAppointPersonWithEmptyBookThrowException() {
        // given
        when(booksRepository.findById(anyInt())).thenReturn(Optional.empty());
        // when
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> booksService.appointPerson(testPerson, TEST_ID)
        );
        // then
        assertEquals("Book not found", ex.getMessage());
        verify(booksRepository).findById(anyInt());
    }

    @Test
    public void testDelete() {
        // given, when
        for (int i = 0; i < 5; i++) booksService.delete(i);
        // then
        verify(booksRepository, times(5)).deleteById(anyInt());
    }

    @Test
    public void testFree() {
        // given
        testBook.setReader(testPerson);
        testBook.setTakenAt(new Date());
        when(booksRepository.findById(TEST_ID)).thenReturn(Optional.of(testBook));
        // when
        booksService.free(TEST_ID);
        // then
        assertNull(testBook.getReader());
        assertNull(testBook.getTakenAt());
        verify(booksRepository).findById(TEST_ID);
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(booksRepository);
    }
}
