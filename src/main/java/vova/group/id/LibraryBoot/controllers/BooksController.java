package vova.group.id.LibraryBoot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vova.group.id.LibraryBoot.models.Book;
import vova.group.id.LibraryBoot.models.BookPageForm;
import vova.group.id.LibraryBoot.models.Person;
import vova.group.id.LibraryBoot.services.BooksService;
import vova.group.id.LibraryBoot.services.PeopleService;


@Controller
@RequestMapping("/library/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService personService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService personService) {
        this.booksService = booksService;
        this.personService = personService;
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
            model.addAttribute("people", personService.index());
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
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        booksService.save(book);
        return "redirect:/library/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.show(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult, @PathVariable("id") int id) {


        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        booksService.update(id, book);
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
}
