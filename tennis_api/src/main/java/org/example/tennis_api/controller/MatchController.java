package org.example.tennis_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.tennis_api.dto.match.MatchDTO;
import org.example.tennis_api.entity.Match;
import org.example.tennis_api.mapper.MatchMapper;
import org.example.tennis_api.service.MatchService;
import org.example.tennis_api.utilities.CsvExportStrategy;
import org.example.tennis_api.utilities.MatchExportStrategy;
import org.example.tennis_api.utilities.TxtExportStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final MatchMapper matchMapper;

    @PostMapping("/")
    public ResponseEntity<Match> createMatch(@RequestBody MatchDTO matchDTO) {
        try {
            Match match = matchService.createMatch(matchDTO);
            return ResponseEntity.ok(match);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{matchId}/register/")
    public ResponseEntity<MatchDTO> registerPlayerToMatch(@PathVariable Integer matchId, @RequestParam Integer playerId) {
        try {
            Match match = matchService.registerPlayerToMatch(matchId, playerId);
            return ResponseEntity.ok(matchMapper.toDTO(match));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchService.findAllMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatchById(@PathVariable Integer id) {
        try {
            Optional<Match> match = matchService.findMatchById(id);
            return match.map(value -> ResponseEntity.ok(matchMapper.toDTO(value)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(@PathVariable Integer id, @RequestBody MatchDTO matchDTO) {
        try {
            Match updatedMatch = matchService.updateMatch(matchDTO, id);
            return ResponseEntity.ok(updatedMatch);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Integer id) {
        try {
            matchService.deleteMatch(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{matchId}/score")
    public ResponseEntity<MatchDTO> updateMatchScore(@PathVariable Integer matchId, @RequestParam Integer player1Score, @RequestParam Integer player2Score) {
        try {
            matchService.updateMatchScore(matchId, player1Score, player2Score);
            Match updatedMatch = matchService.findMatchById(matchId).orElseThrow(() -> new Exception("Match not found after update"));
            return ResponseEntity.ok(matchMapper.toDTO(updatedMatch));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMatches(@RequestParam(required = false) LocalDate startDate,
                                                @RequestParam(required = false) LocalDate endDate,
                                                @RequestParam(required = false) String location,
                                                @RequestParam(required = false) Integer refereeId,
                                                @RequestParam(required = false) Integer playerId,
                                                @RequestParam String format) {
        try {
            List<Match> matches = filterMatches(startDate, endDate, location, refereeId, playerId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatchExportStrategy strategy = format.equals("csv") ? new CsvExportStrategy() : new TxtExportStrategy();
            strategy.export(matches, outputStream);
            byte[] data = outputStream.toByteArray();
            return ResponseEntity.ok()
                    .contentType(format.equals("csv") ? MediaType.valueOf("text/csv") : MediaType.valueOf("text/plain"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"matches." + format + "\"")
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private List<Match> filterMatches(LocalDate startDate, LocalDate endDate, String location, Integer refereeId, Integer playerId) {
        List<Match> matches = new ArrayList<>();
        if (startDate != null && endDate != null) {
            matches.addAll(matchService.findMatchesByDateRange(startDate, endDate));
        } else if (location != null) {
            matches.addAll(matchService.findMatchesByLocation(location));
        } else if (refereeId != null) {
            matches.addAll(matchService.findMatchesByReferee(refereeId));
        } else if (playerId != null) {
            matches.addAll(matchService.findMatchesByPlayer(playerId));
        } else {
            matches = matchService.findAllMatches();
        }
        return matches;
    }
}
