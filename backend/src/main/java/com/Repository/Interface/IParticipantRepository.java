package com.Repository.Interface;

import com.Domain.Participant;

import java.util.ArrayList;

public interface IParticipantRepository extends RepositoryInterface<Participant>{
    /** Fetch all participants enrolled in the given competition */
    ArrayList<Participant> getParticipantsForCompetition(int competitionID);
}
