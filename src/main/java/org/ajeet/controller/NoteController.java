package org.ajeet.controller;

import jakarta.validation.Valid;
import org.ajeet.dto.NoteRequest;
import org.ajeet.dto.NoteResponse;
import org.ajeet.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteResponse> getAll(@AuthenticationPrincipal UserDetails principal) {
        return noteService.getAll(principal.getUsername());
    }

    @GetMapping("/{id}")
    public NoteResponse getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        return noteService.getById(id, principal.getUsername());
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.create(request, principal.getUsername()));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable Long id,
                               @Valid @RequestBody NoteRequest request,
                               @AuthenticationPrincipal UserDetails principal) {
        return noteService.update(id, request, principal.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails principal) {
        noteService.delete(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
