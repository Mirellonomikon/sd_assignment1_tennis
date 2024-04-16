package org.example.tennis_api.dto.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class MatchDTO {
    private String name;
    private LocalDate matchDate;
    private LocalTime matchTime;
    private String location;
    private Integer tournament;
    private Integer referee;
    private Integer player1;
    private Integer player1Score;
    private Integer player2;
    private Integer player2Score;
}
