package vova.group.id.LibraryBoot.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonDTO {
    private int id;

    @NotEmpty(message = "Enter full name")
    @Pattern(regexp = "^([A-Za-z]+(?:\\s[A-Za-z]+)+|[А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+(?: [А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+)+(?: [А-ЩЬЮЯЇІЄҐ][а-щьюяїієґ']+){1,2})$",
            message = "Enter the name in the format \"Johnny Cash\" or \"Остапенко Андрій Вікторович\"")
    @Size(min = 1, max = 150, message = "name must be between 1 and 150 characters")
    private String fullName;

    @NotEmpty(message = "Year of birth cannot be empty")
    @Pattern(regexp = "\\d{4}", message = "Enter a valid 4-digit year")
    private String birthYear;

    @NotEmpty(message = "Enter Email")
    @Email(message = "Enter correct Email")
    @Size(min = 1, max = 150, message = "Email must be between 1 and 150 characters")
    private String email;

    public PersonDTO() {
    }

    public PersonDTO(String fullName, String birthYear, String email) {
        this.fullName = fullName;
        this.birthYear = birthYear;
        this.email = email;
    }
}
