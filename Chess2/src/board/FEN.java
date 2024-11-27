package board;

public class FEN {
	public String piecePlacement;
	public char activeColor;
	public String castlingAvailible;
	public String enPassant;
	public int halfMoves;
	public int fullMoves;
	
	public FEN(String fen) {
		String[] splitFEN = fen.split(" ");
		this.piecePlacement = splitFEN[0];
		this.activeColor = splitFEN[1].charAt(0);
		this.castlingAvailible = splitFEN[2];
		this.enPassant = splitFEN[3];
		this.halfMoves = Integer.parseInt(splitFEN[4]);
		this.fullMoves = Integer.parseInt(splitFEN[5]);
	}
}
