package vova.group.id.LibraryBoot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Pattern(regexp = "^([A-Za-z]+(?:\\s[A-Za-z]+)+|[А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+(?: [А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+)+(?: [А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+){1,2})$",
            message = "Enter the name in the format \"Johnny Cash\" or \"Остапенко Андрій Вікторович\"")
    @Size(min = 1, max = 150, message = "name must be between 1 and 150 characters")
    @Column(name = "full_name")
    private String fullName;

    @Min(value = 1900, message = "Year of birth must be greater than 1900")
    @Column(name = "birth_year")
    private int birthYear;

    @NotEmpty(message = "Enter Email")
    @Email(message = "Enter correct Email")
    @Size(min = 1, max = 150, message = "Email must be between 1 and 150 characters")
    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "reader")
    private List<Book> books;

    public Person () {

    }

    public Person(String fullName, int birthYear, String email) {
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
