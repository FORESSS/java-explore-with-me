package ru.practicum.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column
    private String annotation;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @NotNull
    @Column
    private LocalDateTime createdOn;
    @NotBlank
    @Column
    private String description;
    @NotNull
    @Column
    private LocalDateTime eventDate;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;
    @NotNull
    @Column
    private Boolean paid;
    @NotNull
    @Column
    private Long participantLimit;
    @NotNull
    @Column
    private LocalDateTime publishedOn;
    @NotNull
    @Column
    private Boolean requestModeration;
    @NotNull
    @Column
    private @Enumerated(value = EnumType.STRING)
    State state;
    @NotBlank
    @Column
    private String title;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
    @Transient
    private Long views;
}