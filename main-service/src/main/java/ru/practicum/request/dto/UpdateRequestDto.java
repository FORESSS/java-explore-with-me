package ru.practicum.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.model.Status;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {
    @NotNull
    private Set<Long> requestIds;
    @NotNull
    private Status status;
}