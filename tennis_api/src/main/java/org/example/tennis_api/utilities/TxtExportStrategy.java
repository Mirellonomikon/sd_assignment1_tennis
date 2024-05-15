package org.example.tennis_api.utilities;

import org.example.tennis_api.entity.Match;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class TxtExportStrategy implements MatchExportStrategy {
    @Override
    public void export(List<Match> matches, OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        for (Match match : matches) {
            String block = String.format("Match: %s\nDate: %s\nTime: %s\nLocation: %s\nReferee: %s\nPlayer 1: %s\nScore: %d\nPlayer 2: %s\nScore: %d\n\n",
                    match.getName(),
                    match.getMatchDate(),
                    match.getMatchTime(),
                    match.getLocation(),
                    match.getReferee() != null ? match.getReferee().getName() : "N/A",
                    match.getPlayer1() != null ? match.getPlayer1().getName() : "N/A",
                    match.getPlayer1Score() != null ? match.getPlayer1Score() : 0,
                    match.getPlayer2() != null ? match.getPlayer2().getName() : "N/A",
                    match.getPlayer2Score() != null ? match.getPlayer2Score() : 0);
            writer.write(block);
        }

        writer.flush();
    }
}
