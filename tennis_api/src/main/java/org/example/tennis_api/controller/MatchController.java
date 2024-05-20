package org.example.tennis_api.controller;

import lombok.RequiredArgsConstructor;
import org.example.tennis_api.dto.match.MatchDTO;
import org.example.tennis_api.entity.Match;
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
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

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
        Match match = matchService.findMatchById(id);
        return ResponseEntity.ok(match);
    }

    @GetMapping("/ref/{ref}")
    public ResponseEntity<List<Match>> getMatchByRef(@PathVariable Integer ref) throws Exception {
        List<Match> matches = matchService.findAllMatchesByRefereeId(ref);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Match>> getMatchesByPlayerId(@PathVariable Integer playerId) {
        List<Match> matches = matchService.findAllMatchesByPlayerId(playerId);
        return ResponseEntity.ok(matches);
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

    @PutMapping("/{matchId}/remove/{playerId}")
    public ResponseEntity<Match> removePlayerFromMatch(@PathVariable Integer matchId, @PathVariable Integer playerId) {
        Match updatedMatch = matchService.removePlayerFromMatch(matchId, playerId);
        return ResponseEntity.ok(updatedMatch);
    }

    @PutMapping("/{matchId}/score")
    public ResponseEntity<Match> updateMatchScore(@PathVariable Integer matchId, @RequestBody Map<String, Integer> scoreData) throws Exception {
        Integer player1Score = scoreData.getOrDefault("player1Score", null);
        Integer player2Score = scoreData.getOrDefault("player2Score", null);

        Match updatedMatch = matchService.updateMatchScore(matchId, player1Score, player2Score);
        return ResponseEntity.ok(updatedMatch);
    }

    @GetMapping("/filter/matches")
    public ResponseEntity<List<Match>> filterMatches(@RequestParam(required = false) LocalDate startDate,
                                                     @RequestParam(required = false) LocalDate endDate,
                                                     @RequestParam(required = false) String location,
                                                     @RequestParam(required = false) Integer refereeId,
                                                     @RequestParam(required = false) Integer playerId) {

        List<Match> matches = matchService.findMatches(startDate, endDate, location, refereeId, playerId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMatches(
            @RequestParam(required = false) LocalDate startDate,
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
