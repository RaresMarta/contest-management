package com.DTO;

import com.Domain.Participant;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class EnrollDTO {
    private Participant participant;
    private List<String> compTypes;
}
