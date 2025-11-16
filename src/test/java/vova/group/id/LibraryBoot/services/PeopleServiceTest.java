package vova.group.id.LibraryBoot.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.repositories.PeopleRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
public class PeopleServiceTest {
    @MockitoBean
    private PeopleRepository peopleRepository;

    @Autowired
    private PeopleService peopleService;

    private Person testPerson;
    private Book oldBook;
    private Book newBook;
    private List<Person> testPeople;

    @BeforeEach
    public void setUp() {
        oldBook = new Book();
        oldBook.setTakenAt(daysAgo(11));
        oldBook.setExpired(false);// older than 10 days
        newBook = new Book();
        newBook.setTakenAt(daysAgo(3));
        newBook.setExpired(false);
        testPerson = new Person();
        testPerson.setBooks(List.of(oldBook, newBook));// within 10 days
        testPeople = Collections.singletonList(testPerson);
    }

    private Date daysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }

    @Test
    public void testIndex() {
        // given
        when(peopleRepository.findAll()).thenReturn(testPeople);
        // when
        List<Person> receivedPeople = peopleService.index();
        // then
        assertIterableEquals(testPeople, receivedPeople);
        verify(peopleRepository).findAll();
    }

    @Test
    public void testShowById() {
        // given
        when(peopleRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        // when
        Person receivedPerson = peopleService.show(anyInt());
        // then
        assertEquals(testPerson, receivedPerson);
        verify(peopleRepository).findById(anyInt());
    }

    @Test
    public void testShowByEmail() {
        // given
        when(peopleRepository.findByEmail(anyString())).thenReturn(testPerson);
        // when
        Person receivedPerson = peopleService.show(anyString());
        // then
        assertEquals(testPerson, receivedPerson);
        verify(peopleRepository).findByEmail(anyString());
    }

    @Test
    public void testShowPersonBookWithException() {
        // given
        when(peopleRepository.findById(anyInt())).thenReturn(Optional.empty());
        // when
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> peopleService.showPersonBooks(anyInt())
        );
        // then
        assertEquals("Person not found", ex.getMessage());
        verify(peopleRepository).findById(anyInt());
    }

    @Test
    public void testShowPersonBookWithCorrectExpireDate() {
        // given
        testPerson.setBooks(List.of(oldBook, newBook));
        when(peopleRepository.findById(anyInt())).thenReturn(Optional.of(testPerson));
        // when
        List<Book> receivedBooks = peopleService.showPersonBooks(anyInt());
        // then
        assertEquals(2, receivedBooks.size());
        assertTrue(receivedBooks.get(0).getExpired());
        assertFalse(receivedBooks.get(1).getExpired());
        verify(peopleRepository).findById(anyInt());
    }

    @Test
    public void testSave() {
        // given, when
        for (int i = 0; i < 5; i++) peopleService.save(new Person());
        // then
        verify(peopleRepository, times(5)).save(any(Person.class));
    }

    @Test
   public void updateTest() {
        // given, when
        peopleService.update(anyInt(), testPerson);
        // then
        verify(peopleRepository, times(1)).save(testPerson);
    }

    @Test
    public void deleteTest() {
        // given, when
        for (int i = 0; i < 7; i++) peopleService.delete(anyInt());
        // then
        verify(peopleRepository, times(7)).deleteById(anyInt());
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(peopleRepository);
    }
}
