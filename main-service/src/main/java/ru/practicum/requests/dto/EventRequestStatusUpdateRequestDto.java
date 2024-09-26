package ru.practicum.requests.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.Status;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    private Set<Long> requestIds;
    @NotNull
    private Status status;
}