package org.example.tennis_api.utilities;

import org.example.tennis_api.entity.Match;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvExportStrategy implements MatchExportStrategy {
    @Override
    public void export(List<Match> matches, OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write("Name,Match Date,Match Time,Location,Referee,Player 1,Player 1 Score,Player 2,Player 2 Score\n");

        for (Match match : matches) {
            String line = String.format("%s,%s,%s,%s,%s,%s,%d,%s,%d\n",
                    match.getName(),
                    match.getMatchDate(),
                    match.getMatchTime(),
                    match.getLocation(),
                    match.getReferee() != null ? match.getReferee().getName() : "N/A",
                    match.getPlayer1() != null ? match.getPlayer1().getName() : "N/A",
                    match.getPlayer1Score() != null ? match.getPlayer1Score() : 0,
                    match.getPlayer2() != null ? match.getPlayer2().getName() : "N/A",
                    match.getPlayer2Score() != null ? match.getPlayer2Score() : 0);
            writer.write(line);
        }

        writer.flush();
    }
}