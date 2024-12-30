package userInterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

import engine.TimeManagement;
import engine.minimax;
import board.Move;
import board.Position;
import moveGeneration.MoveGenerator;


public class GameGUI implements ActionListener{
	JFrame frame;
	JPanel boardPanel;
	JButton[] buttonArray = new JButton[64];

	JPanel timerPanel;
	Timer timer;
	JLabel whiteTimerLabel;
	JLabel blackTimerLabel;
	Position position;
	List<Move> legalMoves;
	List<Move> movesFromSelected;
	long whiteTime;
	long blackTime;
	long initialTime;
	int moveStart;
	int moveDestination;
	int selectedSquare;




	public GameGUI(Position position, GameSettings gameSettings) {
		this.position = position;

		this.frame = new JFrame();
		this.boardPanel = new JPanel();
		this.timerPanel = new JPanel();

		timer = new Timer(1000, e -> {
            if (position.activePlayer == board.Color.WHITE) {
                whiteTime -= 1000;
                // handle timeout here
                whiteTimerLabel.setText(getTimeString(whiteTime));
                whiteTimerLabel.repaint();
            } else {
                blackTime -= 1000;
                // handle timeout here
                blackTimerLabel.setText(getTimeString(blackTime));
                blackTimerLabel.repaint();
            }
        });


		initialTime = gameSettings.millisTime;
		whiteTime = initialTime;
		blackTime = initialTime;

		whiteTimerLabel = new JLabel();
		blackTimerLabel = new JLabel();
		whiteTimerLabel.setFont(new Font("Serif", Font.PLAIN, 25));
		blackTimerLabel.setFont(new Font("Serif", Font.PLAIN, 25));
		whiteTimerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		blackTimerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		whiteTimerLabel.setText(getTimeString(whiteTime));
		blackTimerLabel.setText(getTimeString(blackTime));

		timerPanel.add(whiteTimerLabel);
		timerPanel.add(blackTimerLabel);
		//timerPanel.add(new JButton("flip"));


		frame.add(timerPanel, BorderLayout.PAGE_START);



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
			boardPanel.add(buttonArray[i]);
		}
		boardPanel.setPreferredSize(new Dimension(500,500));
		boardPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		boardPanel.setLayout(new GridLayout(8,8));
		frame.add(boardPanel, BorderLayout.PAGE_END);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		this.updateDisplay();
		movesFromSelected = new LinkedList<Move>();
		selectedSquare = -1;
		legalMoves = MoveGenerator.generateStrictlyLegal(position);

		timer.start();
	}
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
	/**
	* Applies a human move
	*/
	private void applyMove(Move move) {
		legalMoves.clear();
		position.makeMove(move);
		updateDisplay();

		computerMove();


	}

	/**
	* generates and applies a computer move
	*/
	private void computerMove() {
		SwingWorker<Move, Void> worker = new SwingWorker<>() {
			@Override
			protected Move doInBackground() throws Exception {
				return minimax.iterativeDeepening(position, TimeManagement.millisForMove(
					position.activePlayer == board.Color.WHITE ? whiteTime : blackTime)).bestMove;
			}

			@Override
			protected void done() {
				try {
					Move computerMove = get();
					position.makeMove(computerMove);
					updateDisplay();
					legalMoves.clear();
					legalMoves.addAll(MoveGenerator.generateStrictlyLegal(position));
					resetSquares();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		worker.execute();

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
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_White_Pawn.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[1] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_White_Knight.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[2] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_White_Bishop.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[3] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_White_Rook.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[4] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_White_Queen.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[5] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_White_King.png"));
					buttonArray[jpanelPos].setIcon(icon);
				}
			} else if ((position.pieceColors[1] & (1L << i)) != 0) {
				if((position.pieces[0] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_Black_Pawn.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[1] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_Black_Knight.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[2] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_Black_Bishop.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[3] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_Black_Rook.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[4] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_Black_Queen.png"));
					buttonArray[jpanelPos].setIcon(icon);
				} else if((position.pieces[5] & (1L << i)) != 0) {
					icon = new ImageIcon(getClass().getResource("/pngImages/Chess_Black_King.png"));
					buttonArray[jpanelPos].setIcon(icon);
				}
			}
		}
	}

	private int littleEndianToJPanel(int endian) {
		int endianRow = endian / 8;
		int jpanelRow = 7 - endianRow;
		int jpanel = jpanelRow * 8 + endian % 8;
		return jpanel;
	}

	private String getTimeString(long time) {
		long totalSeconds = time / 1000;
		long seconds = totalSeconds % 60;
		long minutes = totalSeconds / 60;
		return String.format("%02d:%02d", minutes, seconds);
	}
}
