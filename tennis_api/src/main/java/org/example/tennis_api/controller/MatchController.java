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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final MatchMapper matchMapper;

    @PostMapping("/create")
    public ResponseEntity<Match> createMatch(@RequestBody MatchDTO matchDTO) throws Exception {
        Match match = matchService.createMatch(matchDTO);
        return ResponseEntity.ok(match);
    }

    @PutMapping("/{matchId}/register")
    public ResponseEntity<Match> registerPlayerToMatch(@PathVariable Integer matchId, @RequestParam Integer playerId) throws Exception {
        Match match = matchService.registerPlayerToMatch(matchId, playerId);
        return ResponseEntity.ok(match);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchService.findAllMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Integer id) {
        Match match = matchService.findMatchById(id).orElseThrow(() -> new InputMismatchException("Match not found"));
        return ResponseEntity.ok(match);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(@PathVariable Integer id, @RequestBody MatchDTO matchDTO) throws Exception {
        Match updatedMatch = matchService.updateMatch(matchDTO, id);
        return ResponseEntity.ok(updatedMatch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Integer id) throws Exception {
        matchService.deleteMatch(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{matchId}/score")
    public ResponseEntity<Match> updateMatchScore(@PathVariable Integer matchId, @RequestParam Integer player1Score, @RequestParam Integer player2Score) throws Exception {
        matchService.updateMatchScore(matchId, player1Score, player2Score);
        Match updatedMatch = matchService.findMatchById(matchId).orElseThrow(() -> new Exception("Match not found after update"));
        return ResponseEntity.ok(updatedMatch);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMatches(@RequestParam(required = false) LocalDate startDate,
                                                @RequestParam(required = false) LocalDate endDate,
                                                @RequestParam(required = false) String location,
                                                @RequestParam(required = false) Integer refereeId,
                                                @RequestParam(required = false) Integer playerId,
                                                @RequestParam String format) throws Exception {
        List<Match> matches = matchService.findMatches(startDate, endDate, location, refereeId, playerId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatchExportStrategy strategy = format.equals("csv") ? new CsvExportStrategy() : new TxtExportStrategy();
        matchService.exportMatches(matches, outputStream, strategy);
        byte[] data = outputStream.toByteArray();
        return ResponseEntity.ok()
                .contentType(format.equals("csv") ? MediaType.valueOf("text/csv") : MediaType.valueOf("text/plain"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"matches." + format + "\"")
                .body(data);
    }
}
