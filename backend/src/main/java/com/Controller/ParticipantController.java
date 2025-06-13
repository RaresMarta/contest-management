package com.Controller;

import com.Domain.Participant;
import com.Service.ParticipantService;
import com.DTO.ParticipantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/participants")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    /** CREATE */
    @PostMapping
    public ResponseEntity<Participant> create(@RequestBody ParticipantDTO dto) {
        Participant newParticipant = new Participant(null, dto.getName(), dto.getAge());
        Participant saved = participantService.add(newParticipant);
        URI location = URI.create("/api/participants/" + saved.getParticipantID());
        return ResponseEntity.created(location).body(saved);
    }

    /** GET ALL */
    @GetMapping
    public ResponseEntity<List<Participant>> getAll() {
        List<Participant> participants = participantService.getAll();
        return participants.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(participants);
    }

    /** GET BY ID */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable int id) {
        Participant found = participantService.getById(id);
        return found != null
                ? ResponseEntity.ok(found)
                : ResponseEntity.notFound().build();
    }

    /** UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<Participant> update(
            @PathVariable int id,
            @RequestBody ParticipantDTO updatedDTO) {
        Participant existing = participantService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setName(updatedDTO.getName());
        existing.setAge(updatedDTO.getAge());
        participantService.update(id, existing);
        return ResponseEntity.ok(existing);
    }

    /** DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        participantService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comp/{id}")
    public ResponseEntity<List<Participant>> getParticipantsForComp(@PathVariable int id) {
        List<Participant> participants = participantService.getParticipantsForCompetition(id);
        return participants.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(participants);
    }
}
