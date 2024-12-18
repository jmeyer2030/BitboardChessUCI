package board;

public enum Color {
    WHITE,
    BLACK;

    public static Color flipColor (Color color) {
        return color == WHITE ? BLACK : WHITE;
    }
}
