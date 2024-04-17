package org.example.tennis_api.utilities;

import org.example.tennis_api.entity.Match;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface MatchExportStrategy {
    void export(List<Match> matches, OutputStream outputStream) throws IOException;
}
