package com.Service;

import com.Domain.Participant;
import com.Repository.Interface.IParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final IParticipantRepository participantRepo;

    // ----- CRUD -----
    public Participant add(Participant p) {
        return participantRepo.add(p);
    }

    public List<Participant> getAll() {
        return participantRepo.getAll();
    }

    public Participant getById(int id) {
        return participantRepo.getById(id);
    }

    public void update(int id, Participant p) {
        participantRepo.update(id, p);
    }

    public void remove(int id) {
        participantRepo.remove(id);
    }

    /** Get all participants for a specific competition.
     * @param competitionID The ID of the competition.
     * @return A list of participants enrolled in the specified competition.
     */
    public ArrayList<Participant> getParticipantsForCompetition(int competitionID) {
        return participantRepo.getParticipantsForCompetition(competitionID);
    }
}
