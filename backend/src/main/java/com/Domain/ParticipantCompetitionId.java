package com.Domain;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantCompetitionId implements Serializable {
    private Integer participantID;
    private Integer competitionID;
}
