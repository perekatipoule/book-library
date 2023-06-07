package vova.group.id.LibraryBoot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.BookPageForm;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.repositories.BooksRepository;


import java.util.*;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> index(BookPageForm form) {
        List<Book> books = new ArrayList<>();

        Boolean sortByYear = form.getSortByYear();

        Integer page = Optional.ofNullable(form.getPage())
                .filter(pageValue -> !pageValue.isEmpty())
                .map(Integer::parseInt)
                .orElse(null);

        Integer booksPerPage = Optional.ofNullable(form.getBooksPerPage())
                .filter(booksValue -> !booksValue.isEmpty())
                .map(Integer::parseInt)
                .orElse(null);

        if (page != null && booksPerPage != null && sortByYear != null && sortByYear)
            books = booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else if (page != null && booksPerPage != null)
            books = booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
        else if (sortByYear != null && sortByYear)
            books = booksRepository.findAll(Sort.by("year"));
        else
            books = booksRepository.findAll();


        return books;
    }

    public List<Book> index(String titleFragment) {
        return booksRepository.findByTitleContainingIgnoreCase(titleFragment);
    }



    public Book show(int id) {
        return booksRepository.findById(id).orElse(null);
    }

    public Person showBookReader(int id) {
        return booksRepository.findById(id).map(Book::getReader).orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }


    @Transactional
    public void update(int id, Book updatedBook) {
        Book bookToBeUpdated = booksRepository.findById(id).get();

        updatedBook.setId(id);
        updatedBook.setReader(bookToBeUpdated.getReader());

        booksRepository.save(updatedBook);
    }

    @Transactional
    public void appointPerson(Person person, int bookId) {
        Book book = booksRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.setReader(person);
        book.setTakenAt(new Date());
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void free(int id) {
        Book book = booksRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.setReader(null);
        book.setTakenAt(null);
    }

}
