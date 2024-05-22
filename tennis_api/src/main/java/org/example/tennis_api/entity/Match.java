package org.example.tennis_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "match_time", nullable = false)
    private LocalTime matchTime;

    @Column(name = "location", nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "referee_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User referee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player1_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User player1;

    @Column(name = "player1_score")
    private Integer player1Score;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player2_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User player2;

    @Column(name = "player2_score")
    private Integer player2Score;
}