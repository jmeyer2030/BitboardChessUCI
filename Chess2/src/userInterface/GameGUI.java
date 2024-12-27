package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import engine.minimax;
import board.Move;
import board.Position;
import moveGeneration.MoveGenerator;

public class GameGUI implements ActionListener{
	JLabel label;
	JFrame frame;
	JPanel panel;
	JButton[] buttonArray = new JButton[64];
	Position position;
	List<Move> legalMoves;
	List<Move> movesFromSelected;
	int moveStart;
	int moveDestination;
	int selectedSquare;

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < 64; i++) {
			if (e.getSource() != buttonArray[i])
				continue;

			int clickedSquare = littleEndianToJPanel(i);

			// Reset highlighted squares after every button press
			resetSquares();

			// If no square is selected, set the clicked square as the selected one
			if (selectedSquare == -1) {
				movesFromSelected.clear();
				selectedSquare = clickedSquare;
				// Highlight the legal moves from the selected square
				for (Move move : legalMoves) {
					if (move.start == selectedSquare)
					movesFromSelected.add(move);

				}
				highlightSquares(movesFromSelected);
			} else {
				// If a square is already selected, check if the move is legal
				Optional<Move> moveToApply = movesFromSelected.stream()
						.filter(move -> move.destination == clickedSquare)
						.findFirst();

				movesFromSelected.clear();

				if (moveToApply.isPresent()) {
					// Apply the move if it's legal
					applyMove(moveToApply.get());
					resetSquares(); // Reset squares after applying the move
					selectedSquare = -1;
				} else {
					// If the clicked square is not a legal move, highlight legal moves from this new square
					movesFromSelected.clear();
					selectedSquare = clickedSquare;
					for (Move move : legalMoves) {
						if (move.start == selectedSquare)
							movesFromSelected.add(move);

					}
					highlightSquares(movesFromSelected);
				}
			}
			System.out.println("legal moves size: " + legalMoves.size());
			System.out.println("selected size: " + movesFromSelected.size());
		}
	}


	private void applyMove(Move move) {
		position.makeMove(move);
		updateDisplay();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Move computerMove = minimax.iterativeDeepening(position, 5_000).bestMove;

		position.makeMove(computerMove);

		updateDisplay();

		legalMoves.clear();

		resetSquares();

		legalMoves.addAll(MoveGenerator.generateStrictlyLegal(position));
	}

	private int littleEndianToJPanel(int endian) {
		int endianRow = endian / 8;
		int jpanelRow = 7 - endianRow;
		int jpanel = jpanelRow * 8 + endian % 8;
		return jpanel;
	}
	
	public GameGUI(Position position) {
		this.position = position;
		frame = new JFrame();
		panel = new JPanel();
		for (int i = 0; i < 64; i++) {
			buttonArray[i] = new JButton();
			if (((i + (i / 8)) % 2) == 1) {
				buttonArray[i].setBackground(Color.GRAY);
				buttonArray[i].setForeground(Color.WHITE);
			} else {
				buttonArray[i].setBackground(Color.WHITE);
				buttonArray[i].setForeground(Color.BLACK);
			}
			buttonArray[i].addActionListener(this);
			panel.add(buttonArray[i]);
		}
		panel.setPreferredSize(new Dimension(450,450));
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		panel.setLayout(new GridLayout(8,8));
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		this.updateDisplay();
		movesFromSelected = new LinkedList<Move>();
		selectedSquare = -1;
		legalMoves = MoveGenerator.generateStrictlyLegal(position);
	}
	public void highlightSquares(List<Move> moves) {
		for (Move move : moves) {
			buttonArray[littleEndianToJPanel(move.destination)].setBackground(new Color(115, 125, 215));
		}
	}

	public void resetSquares() {
		for (int i = 0; i < 64; i++) {
			if (((i + (i / 8)) % 2) == 1) {
				buttonArray[i].setBackground(Color.GRAY);
				buttonArray[i].setForeground(Color.WHITE);
			} else {
				buttonArray[i].setBackground(Color.WHITE);
				buttonArray[i].setForeground(Color.BLACK);
			}
		}
	}

	public void updateDisplay() {
		Icon icon;
		for (int i = 0; i < 64; i++) {
			int jpanelPos = littleEndianToJPanel(i);
			buttonArray[jpanelPos].setIcon(null);
			if ((position.pieceColors[0] & (1L << i)) != 0) {
				if((position.pieces[0] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_White_Pawn.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[1] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_White_Knight.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[2] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_White_Bishop.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[3] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_White_Rook.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[4] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_White_Queen.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[5] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_White_King.png");
					buttonArray[jpanelPos].setIcon(icon);
				}
			} else if ((position.pieceColors[1] & (1L << i)) != 0) {
				if((position.pieces[0] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_Black_Pawn.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[1] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_Black_Knight.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[2] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_Black_Bishop.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[3] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_Black_Rook.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[4] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_Black_Queen.png");
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[5] & (1L << i)) != 0) {
					icon = new ImageIcon("C:\\Users\\jmeye\\Pictures\\ChessPieces\\Chess_Black_King.png");
					buttonArray[jpanelPos].setIcon(icon);
				}
			}
		}
	}
}
