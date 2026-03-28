package com.jmeyer.bench;

/**
 * Represents a single Lichess puzzle parsed from the CSV database.
 *
 * <p>CSV format:
 * {@code PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl,OpeningTags}
 *
 * <p>The {@code moves} array uses Long Algebraic Notation (LAN). The first move is the opponent's
 * "setup" move that creates the puzzle position; subsequent moves are the solution.
 */
public class Puzzle {
    public final String id;
    public final String fen;
    public final String[] moves;
    public final int rating;
    public final int ratingDeviation;
    public final int popularity;
    public final int nbPlays;
    public final String[] themes;
    public final String gameUrl;
    public final String openingTags;

    public Puzzle(String csvLine) {
        // Split on commas — the CSV has no quoted fields with embedded commas
        String[] fields = csvLine.split(",", -1);
        if (fields.length < 10) {
            throw new IllegalArgumentException("Malformed puzzle CSV line (expected 10 fields, got "
                    + fields.length + "): " + csvLine);
        }

        this.id = fields[0];
        this.fen = fields[1];
        this.moves = fields[2].split(" ");
        this.rating = Integer.parseInt(fields[3]);
        this.ratingDeviation = Integer.parseInt(fields[4]);
        this.popularity = Integer.parseInt(fields[5]);
        this.nbPlays = Integer.parseInt(fields[6]);
        this.themes = fields[7].isEmpty() ? new String[0] : fields[7].split(" ");
        this.gameUrl = fields[8];
        this.openingTags = fields[9];
    }

    /**
     * The LAN of the opponent's setup move (applied to the FEN before searching).
     */
    public String setupMove() {
        return moves[0];
    }

    /**
     * The LAN of the expected best response (what the engine should find).
     */
    public String expectedMove() {
        if (moves.length < 2) {
            throw new IllegalStateException("Puzzle " + id + " has no expected move");
        }
        return moves[1];
    }
}

