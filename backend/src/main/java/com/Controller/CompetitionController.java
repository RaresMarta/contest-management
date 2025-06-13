package com.Controller;

import com.DTO.CompetitionDTO;
import com.DTO.EnrollDTO;
import com.Domain.Competition;
import com.Service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;

    /** CREATE */
    @PostMapping
    public ResponseEntity<Competition> create(@RequestBody CompetitionDTO dto) {
        Competition saved = competitionService.add(new Competition(null, dto.getType(), dto.getAgeCategory(), dto.getNrOfParticipants()));
        URI location = URI.create("/api/competitions/" + saved.getCompetitionID());
        return ResponseEntity.created(location).body(saved);
    }

    /** GET ALL */
    @GetMapping
    public ResponseEntity<List<Competition>> getAll() {
        List<Competition> competitions = competitionService.getAll();
        return competitions.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(competitions);
    }

    /** GET BY ID */
    @GetMapping("/{id}")
    public ResponseEntity<Competition> getById(@PathVariable int id) {
        Competition found = competitionService.getById(id);
        return found != null
                ? ResponseEntity.ok(found)
                : ResponseEntity.notFound().build();
    }

    /** UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<Competition> update(
            @PathVariable int id,
            @RequestBody Competition updated) {
        Competition found = competitionService.getById(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        competitionService.update(id, updated);
        Competition updatedEntity = competitionService.getById(id);
        return ResponseEntity.ok(updatedEntity);
    }

    /** DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        competitionService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}/age/{ageCat}")
    public ResponseEntity<List<Competition>> getByTypeAndAge(
            @PathVariable String type,
            @PathVariable String ageCat) {
        List<Competition> selected = competitionService.getByTypeAndAgeCat(type, ageCat);
        return selected.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(selected);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Competition>> getByType(@PathVariable String type) {
        List<Competition> selected = competitionService.getByType(type);
        return selected.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(selected);
    }

    @GetMapping("/age/{ageCat}")
    public ResponseEntity<List<Competition>> getByAge(@PathVariable String ageCat) {
        List<Competition> selected = competitionService.getByAgeCategory(ageCat);
        return selected.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(selected);
    }

    @PostMapping("/enroll")
    public ResponseEntity<Void> enroll(@RequestBody EnrollDTO dto) {
        ///  EnrollDTO - Participant & Competition Types
        // Participant to enroll
        int partId = dto.getParticipant().getParticipantID();
        int partAge = dto.getParticipant().getAge();

        // Iterate through competition types and enroll
        for (String compType : dto.getCompTypes()) {
            Competition target = competitionService.competitionToEnrollIn(partAge, compType);
            int compId = target.getCompetitionID();
            competitionService.enrollParticipant(partId, compId);
        }
        return ResponseEntity.ok().build();
    }
}
