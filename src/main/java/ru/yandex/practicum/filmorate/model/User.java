package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.RequestMethod;

import java.time.LocalDate;

@Data
public class User {
    @NotNull(groups = RequestMethod.Update.class)
    private Long id;
    @Email(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    @NotBlank(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String email;
    @NotBlank(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String login;
    private String name;
    @PastOrPresent(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private LocalDate birthday;

    public String getName() {
        return name == null || name.isBlank() ? login : name;
    }
}
