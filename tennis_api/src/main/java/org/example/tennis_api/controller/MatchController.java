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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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

    //administrator only
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Match> createMatch(@RequestBody MatchDTO matchDTO) throws Exception {
        Match match = matchService.createMatch(matchDTO);
        return ResponseEntity.ok(match);
    }

    //administrator only
    @PutMapping("/match/register")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Match> registerPlayerToMatch(@RequestParam Integer matchId, @RequestParam Integer playerId) throws Exception {
        Match match = matchService.registerPlayerToMatch(matchId, playerId);
        return ResponseEntity.ok(match);
    }

    //administrator only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchService.findAllMatches();
        return ResponseEntity.ok(matches);
    }

    //referee and administrator only but checks for id to match token one
    @GetMapping("/matchId")
    @PostAuthorize("hasRole('ADMINISTRATOR') or (hasRole('REFEREE') and returnObject.body.referee.id == authentication.principal.id)")
    public ResponseEntity<Match> getMatchById(@RequestParam Integer id) {
        Match match = matchService.findMatchById(id);
        return ResponseEntity.ok(match);
    }

    //referee only but checks for id to match token one
    @GetMapping("/ref")
    @PreAuthorize("hasRole('REFEREE') and (#ref == authentication.principal.id)")
    public ResponseEntity<List<Match>> getMatchByRef(@RequestParam Integer ref) throws Exception {
        List<Match> matches = matchService.findAllMatchesByRefereeId(ref);
        return ResponseEntity.ok(matches);
    }

    //player only but checks for id to match token one
    @GetMapping("/player")
    @PreAuthorize("hasRole('PLAYER') and (#playerId == authentication.principal.id)")
    public ResponseEntity<List<Match>> getMatchesByPlayerId(@RequestParam Integer playerId) {
        List<Match> matches = matchService.findAllMatchesByPlayerId(playerId);
        return ResponseEntity.ok(matches);
    }

    //administrator only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Match> updateMatch(@PathVariable Integer id, @RequestBody MatchDTO matchDTO) throws Exception {
        Match updatedMatch = matchService.updateMatch(matchDTO, id);
        return ResponseEntity.ok(updatedMatch);
    }

    //administrator only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
       public ResponseEntity<Void> deleteMatch(@PathVariable Integer id) throws Exception {
        matchService.deleteMatch(id);
        return ResponseEntity.ok().build();
    }

    //allowed for administrator and player but for player checks for id to match token one
    @PutMapping("/match/remove")
    @PreAuthorize("(hasRole('ADMINISTRATOR')) or (hasRole('PLAYER') and #playerId == authentication.principal.id)")
    public ResponseEntity<Match> removePlayerFromMatch(@RequestParam Integer matchId, @RequestParam Integer playerId) {
        Match updatedMatch = matchService.removePlayerFromMatch(matchId, playerId);
        return ResponseEntity.ok(updatedMatch);
    }

    //referee only, checks for referee attribute of match to be updated to match id of user
    @PutMapping("/match/score")
    @PreAuthorize("hasRole('REFEREE') and @matchServiceImpl.findMatchRef(matchId) == authentication.principal.id")
    public ResponseEntity<Match> updateMatchScore(@RequestParam Integer matchId, @RequestBody Map<String, Integer> scoreData) throws Exception {
        Integer player1Score = scoreData.getOrDefault("player1Score", null);
        Integer player2Score = scoreData.getOrDefault("player2Score", null);

        Match updatedMatch = matchService.updateMatchScore(matchId, player1Score, player2Score);
        return ResponseEntity.ok(updatedMatch);
    }

    //administrator and referee only
    @GetMapping("/filter/matches")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'REFEREE')")
    public ResponseEntity<List<Match>> filterMatches(@RequestParam(required = false) LocalDate startDate,
                                                     @RequestParam(required = false) LocalDate endDate,
                                                     @RequestParam(required = false) String location,
                                                     @RequestParam(required = false) Integer refereeId,
                                                     @RequestParam(required = false) Integer playerId) {

        List<Match> matches = matchService.findMatches(startDate, endDate, location, refereeId, playerId);
        return ResponseEntity.ok(matches);
    }

    //administrator only
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
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
