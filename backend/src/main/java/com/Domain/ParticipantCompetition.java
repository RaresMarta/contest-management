package com.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ParticipantCompetition")
@IdClass(ParticipantCompetitionId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantCompetition {
    @Id
    private Integer participantID;

    @Id
    private Integer competitionID;
}
