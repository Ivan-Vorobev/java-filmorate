package ru.yandex.practicum.filmorate.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.RequestMethod;

@Data
@Builder
public class DirectorDto {
    private Long id;
    @NotBlank(groups = {RequestMethod.Create.class, RequestMethod.Update.class})
    private String name;
}
