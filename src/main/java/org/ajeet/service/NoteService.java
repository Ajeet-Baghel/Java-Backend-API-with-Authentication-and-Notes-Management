package org.ajeet.service;

import org.ajeet.dto.NoteRequest;
import org.ajeet.dto.NoteResponse;
import org.ajeet.model.Note;
import org.ajeet.model.User;
import org.ajeet.repository.NoteRepository;
import org.ajeet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<NoteResponse> getAll(String username) {
        User owner = loadUser(username);
        return noteRepository.findAllByOwnerOrderByCreatedAtDesc(owner)
                .stream()
                .map(NoteResponse::from)
                .toList();
    }

    public NoteResponse getById(Long id, String username) {
        return NoteResponse.from(loadNote(id, username));
    }

    public NoteResponse create(NoteRequest request, String username) {
        Note note = new Note(request.title(), request.content(), loadUser(username));
        return NoteResponse.from(noteRepository.save(note));
    }

    public NoteResponse update(Long id, NoteRequest request, String username) {
        Note note = loadNote(id, username);
        note.setTitle(request.title());
        note.setContent(request.content());
        note.setUpdatedAt(Instant.now());
        return NoteResponse.from(noteRepository.save(note));
    }

    public void delete(Long id, String username) {
        noteRepository.delete(loadNote(id, username));
    }

    private User loadUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private Note loadNote(Long id, String username) {
        User owner = loadUser(username);
        return noteRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found: " + id));
    }
}
