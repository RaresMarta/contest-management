package com.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Competition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Competition {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer competitionID;

    @NonNull
    @Column(nullable = false)
    private String type;

    @NonNull
    @Column(nullable = false)
    private String ageCategory;

    @NonNull
    @Column(nullable = false)
    private int nrOfParticipants;
}