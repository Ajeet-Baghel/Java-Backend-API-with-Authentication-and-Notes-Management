package org.ajeet.repository;

import org.ajeet.model.Note;
import org.ajeet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByOwnerOrderByCreatedAtDesc(User owner);

    Optional<Note> findByIdAndOwner(Long id, User owner);
}
