package com.Repository.Interface;

import com.Domain.Competition;

import java.util.List;

public interface ICompetitionRepository extends RepositoryInterface<Competition> {
    /** Return competitions matching both a type (e.g. "Drawing") and age category (e.g. "6-8 years old") */
    List<Competition> getCompetitionByTypeAndAge(String type, String ageCategory);

    /** Return competitions matching the given type only */
    List<Competition> getCompetitionsByType(String type);

    /** Return competitions matching the given age category only */
    List<Competition> getCompetitionsByAge(String ageCategory);

    /** Increment the nrOfParticipants counter on the competition with the given ID */
    void incrementParticipantCount(int competitionID);

    /** Enroll a participant in a competition */
    void enrollParticipant(int participantID, int competitionID);
}
