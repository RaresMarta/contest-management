package com.Service;

import com.Domain.Competition;
import com.Repository.Interface.ICompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService {
    private final ICompetitionRepository compRepo;

    // ----- CRUD -----
    public Competition add(Competition c) {
        return compRepo.add(c);
    }

    public List<Competition> getAll() {
        return compRepo.getAll();
    }

    public Competition getById(int id) {
        return compRepo.getById(id);
    }

    public void update(int id, Competition c) {
        compRepo.update(id, c);
    }

    public void remove(int id) {
        compRepo.remove(id);
    }

    /** Get competitions by type and age category.
     * @param type    the type of competition (e.g. "Drawing")
     * @param ageCat  the age category (e.g. "6-8 years old")
     * @return a list of competitions matching the criteria
     */
    public List<Competition> getByTypeAndAgeCat(String type, String ageCat) {
        return compRepo.getCompetitionByTypeAndAge(type, ageCat);
    }

    /** Get competitions by type only.
     * @param type the type of competition (e.g. "Drawing")
     * @return a list of competitions matching the type
     */
    public List<Competition> getByType(String type) {
        return compRepo.getCompetitionsByType(type);
    }

    /** Get competitions by age category only.
     *
     * @param ageCat the age category (e.g. "6-8 years old")
     * @return a list of competitions matching the age category
     */
    public List<Competition> getByAgeCategory(String ageCat) {
        return compRepo.getCompetitionsByAge(ageCat);
    }

    /** Find a competition to enroll in based on participant's age and competition type.
     * @param age       the age of the participant
     * @param compType  the type of competition (e.g. "Drawing")
     * @return a Competition object that matches the criteria
     */
    public Competition competitionToEnrollIn(int age, String compType) {
        String ageCategory;
        switch (age) {
            case 6, 7, 8 -> ageCategory = "6-8 years old";
            case 9, 10, 11 -> ageCategory = "9-11 years old";
            case 12, 13, 14, 15-> ageCategory = "12-15 years old";
            default -> {
                return null;
            }
        }
        return compRepo.getCompetitionByTypeAndAge(compType, ageCategory)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /** Associates the participant with the competition.
     * Increments the participant count for the competition.
     * @param participantID The ID of the participant to enroll.
     * @param competitionID The ID of the competition in which to enroll the participant.
     */
    public void enrollParticipant(int participantID, int competitionID) {
        compRepo.enrollParticipant(participantID, competitionID);
        compRepo.incrementParticipantCount(competitionID);
    }
}
