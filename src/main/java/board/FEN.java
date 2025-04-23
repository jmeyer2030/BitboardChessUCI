package board;

public class FEN {
	public final String piecePlacement;
	public final char activeColor;
	public final String castlingAvailible;
	public final String enPassant;
	public final int halfMoves;
	public final int fullMoves;
	
	public FEN(String fen) {
		String[] splitFEN = fen.split(" ");
		assert splitFEN.length == 6;
		this.piecePlacement = splitFEN[0];
		this.activeColor = splitFEN[1].charAt(0);
		this.castlingAvailible = splitFEN[2];
		this.enPassant = splitFEN[3];
		this.halfMoves = Integer.parseInt(splitFEN[4]);
		this.fullMoves = Integer.parseInt(splitFEN[5]);
	}
}
