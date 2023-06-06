package vova.group.id.LibraryBoot.models;


import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class BookPageForm {

    @Pattern(regexp = "\\d+", message = "Page value should be numerical")
    private String page;


    @Pattern(regexp = "\\d+", message = "Books per page value should be numerical")
    private String booksPerPage;


    private Boolean sortByYear;

    public BookPageForm(String page, Integer String, Boolean sortByYear) {
        this.page = page;
        this.booksPerPage = booksPerPage;
        this.sortByYear = sortByYear;
    }

}
