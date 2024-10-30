package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.RequestMethod;

@Data
@Builder
public class Director {
    private Long id;
    @NotBlank(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String name;
}
