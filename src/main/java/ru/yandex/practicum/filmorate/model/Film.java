package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.RequestMethod;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    @NotNull(groups = RequestMethod.Update.class)
    private Long id;
    @NotBlank(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String name;
    @Size(max = 200, groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String description;
    private LocalDate releaseDate;
    @Positive(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    @Min(value = 1, groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private int duration;

    @AssertTrue(message = "Release date invalid", groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    public boolean isValidReleaseDate() {
        return releaseDate.isAfter(START_RELEASE_DATE);
    }
}
