package vova.group.id.LibraryBoot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDTO {
    private int id;

    @NotEmpty(message = "Enter book title")
    @Size(min = 2, max = 150, message = "The title of the book must be between 1 and 150 characters")
    private String title;

    @NotEmpty(message = "Enter author")
    @Size(min = 2, max = 150, message = "The author name must be between 1 and 150 characters")
    private String author;

    @NotEmpty(message = "Year cannot be empty")
    @Pattern(regexp = "\\d{4}", message = "Enter a valid 4-digit year")
    private String year;

    public BookDTO() {
    }

    public BookDTO(String title, String author, String year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }
}
