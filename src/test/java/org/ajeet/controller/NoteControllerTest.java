package org.ajeet.controller;

import org.ajeet.dto.NoteRequest;
import org.ajeet.dto.NoteResponse;
import org.ajeet.service.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    private final UserDetails principal = User.withUsername("ajeet")
            .password("irrelevant")
            .roles("USER")
            .build();

    private final NoteResponse note =
            new NoteResponse(1L, "shopping", "milk and eggs", Instant.now(), Instant.now());

    @Test
    void getAll_returnsNotesOfCurrentUser() {
        when(noteService.getAll("ajeet")).thenReturn(List.of(note));

        List<NoteResponse> result = noteController.getAll(principal);

        assertEquals(1, result.size());
        assertEquals("shopping", result.getFirst().title());
    }

    @Test
    void getById_returnsNote() {
        when(noteService.getById(1L, "ajeet")).thenReturn(note);

        NoteResponse result = noteController.getById(1L, principal);

        assertEquals(1L, result.id());
    }

    @Test
    void create_returnsCreatedNote() {
        NoteRequest request = new NoteRequest("shopping", "milk and eggs");
        when(noteService.create(request, "ajeet")).thenReturn(note);

        ResponseEntity<NoteResponse> result = noteController.create(request, principal);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(note, result.getBody());
    }

    @Test
    void update_returnsUpdatedNote() {
        NoteRequest request = new NoteRequest("shopping", "updated");
        when(noteService.update(1L, request, "ajeet")).thenReturn(note);

        NoteResponse result = noteController.update(1L, request, principal);

        assertEquals(note, result);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> result = noteController.delete(1L, principal);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(noteService).delete(1L, "ajeet");
    }
}
