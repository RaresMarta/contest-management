package com.Domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Participant")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Participant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer participantID;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private Integer age;
}
