package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RequestsRepository extends JpaRepository<Request, Long> {
    List<Request> findByEventId(long eventId);

    List<Request> findByIdIn(Set<Long> ids);

    List<Request> findAllByRequesterId(long id);

    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    List<Request> findAllByEventIdAndStatus(long eventId, Status status);
}