package vova.group.id.LibraryBoot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Person")
@Getter
@Setter
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Enter full name")
    @Pattern(regexp = "^([A-Za-z]+(?:\\s[A-Za-z]+)+|[А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+(?: [А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+)+(?: [А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+){1,2})$",
            message = "Enter the name in the format \"Johnny Cash\" or \"Остапенко Андрій Вікторович\"")
    @Size(min = 1, max = 150, message = "name must be between 1 and 150 characters")
    @Column(name = "full_name")
    private String fullName;

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
}
