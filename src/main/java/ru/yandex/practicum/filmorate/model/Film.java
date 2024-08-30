package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.constraints.ReleaseDateFrom;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    @NotNull(groups = RequestMethod.Update.class)
    private Long id;
    @NotBlank(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String name;
    @Size(max = 200, groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String description;
    @ReleaseDateFrom(from = "1895-12-28", groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private LocalDate releaseDate;
    @Positive(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    @Min(value = 1, groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private int duration;
}
