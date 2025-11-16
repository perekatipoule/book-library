package vova.group.id.LibraryBoot.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vova.group.id.LibraryBoot.dto.BookDTO;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.BookPageForm;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.BooksService;
import vova.group.id.LibraryBoot.services.PeopleService;
import vova.group.id.LibraryBoot.util.BookValidator;


@Controller
@RequestMapping("/library/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService peopleService;
    private final BookValidator bookValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public BooksController(BooksService booksService, PeopleService personService, BookValidator bookValidator, ModelMapper modelMapper) {
        this.booksService = booksService;
        this.peopleService = personService;
        this.bookValidator = bookValidator;
        this.modelMapper = modelMapper;
    }


    @GetMapping()
    public String index(@ModelAttribute("form") @Valid BookPageForm form,
                        BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "books/index";
        }

        model.addAttribute("books", booksService.index(form));

        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {

        model.addAttribute("book", booksService.show(id));

        Person reader = booksService.showBookReader(id);
        if (reader == null) {
            model.addAttribute("people", peopleService.index());
        } else {
            model.addAttribute("bookReader", reader);
        }

        return "books/show";
    }

    @GetMapping("/search")
    public String search() {
        return "books/search";
    }

    @PostMapping("/search")
    public String findBook(@RequestParam("titleFragment") String titleFragment, Model model) {

        // find the books list
        model.addAttribute("searchedBooks", booksService.index(titleFragment));
        return "books/search";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("bookDTO") BookDTO book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("bookDTO") @Valid BookDTO bookDTO,
                         BindingResult bindingResult) {

        bookValidator.validate(bookDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        booksService.save(convertBookDTOToBook(bookDTO));
        return "redirect:/library/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("bookDTO", convertBookToBookDTO(booksService.show(id)));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("bookDTO") @Valid BookDTO bookDTO, BindingResult bindingResult, @PathVariable("id") int id) {

        bookValidator.validate(bookDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        booksService.update(id, convertBookDTOToBook(bookDTO));
        return "redirect:/library/books/" + id;
    }

    @PatchMapping ("/{id}/appoint")
    public String appointPerson(@ModelAttribute("person") Person person, @PathVariable("id") int bookId) {
        booksService.appointPerson(person, bookId);
        return "redirect:/library/books/" + bookId;
    }

    @PatchMapping ("/{id}/free")
    public String free(@PathVariable("id") int id) {
        booksService.free(id);
        return "redirect:/library/books/" + id;
    }


    @DeleteMapping(("/{id}"))
    public String delate(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/library/books";
    }

    public Book convertBookDTOToBook(BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        book.setYear(Integer.parseInt(bookDTO.getYear()));
        return book;
    }

    public BookDTO convertBookToBookDTO(Book book) {
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        bookDTO.setYear(String.valueOf(book.getYear()));
        return bookDTO;
    }
}
