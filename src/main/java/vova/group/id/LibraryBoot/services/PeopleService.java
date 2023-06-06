package vova.group.id.LibraryBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.repositories.BooksRepository;
import vova.group.id.LibraryBoot.repositories.PeopleRepository;


import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final BooksRepository booksRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, BooksRepository booksRepository) {
        this.peopleRepository = peopleRepository;
        this.booksRepository = booksRepository;
    }

    public List<Person> index() {
        return peopleRepository.findAll();
    }

    public Person show(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    public Person show(String email) {
        return peopleRepository.findByEmail(email);
    }


    public List<Book> showPersonBooks(int personId) {
        Person person =  peopleRepository.findById(personId).orElseThrow(() -> new IllegalArgumentException("Person not found"));
        List<Book> personBooks = person.getBooks();
            personBooks.forEach(book -> {
                long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                // 864000000 ms = 10 days
                if (diffInMillies > 864000000)
                    book.setExpired(true); // book is expired
            });
        return personBooks;
    }



    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }


}
