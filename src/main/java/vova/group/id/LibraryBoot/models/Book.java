package vova.group.id.LibraryBoot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Book")
@Getter
@Setter
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Enter book title")
    @Size(min = 2, max = 150, message = "The title of the book must be between 1 and 150 characters")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Enter author")
    @Size(min = 2, max = 150, message = "The author name must be between 1 and 150 characters")
    @Column(name = "author")
    private String author;

    @Min(value = 1400, message = "The year must be more than 1400")
    @Column(name = "book_year")
    private int year;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person reader;

    @Column(name = "taken_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenAt;

    @Transient
    private Boolean expired;

    public Book () {

    }

    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }
}
