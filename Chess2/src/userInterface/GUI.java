package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import board.Move;
import board.Position;
import moveGeneration.MoveGenerator;

public class GUI implements ActionListener{
	JLabel label;
	JFrame frame;
	JPanel panel;
	JButton[][] buttonarray = new JButton[8][8];
	JButton[] buttonArray = new JButton[64];
	JPanel buttonPanel;
	Position position;
	List<Move> legalMoves;
	List<Move> movesFromSelected;
	int moveStart;
	int moveDestination;
	boolean waitingForDestination;
	@Override
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < 64; i++) {
			if (e.getSource() != buttonArray[i])
				continue;
			if (!waitingForDestination) {
				moveStart = littleEndianToJPanel(i);
				movesFromSelected = legalMoves.stream().filter(move -> {
					if (move.start == moveStart)
						return true;
					return false;
				}).collect(Collectors.toList());
				waitingForDestination = true;
			} else {
				moveDestination = littleEndianToJPanel(i);
				Optional<Move> moveToApply = movesFromSelected.stream().filter(move -> {
					if (move.destination == moveDestination)
						return true;
					return false;
				}).findFirst();
				if (moveToApply.isEmpty())
					continue;
				//position;
				updateDisplay();
				waitingForDestination = false;
			}
		}
		
	}

	private int littleEndianToJPanel(int endian) {
		int endianRow = endian / 8;
		int jpanelRow = 7 - endianRow;
		int jpanel = jpanelRow * 8 + endian % 8;
		return jpanel;
	}
	
	public GUI(Position position) {
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
			panel.add(buttonArray[i]);
			buttonArray[i].addActionListener(this);
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
		
		waitingForDestination = false;
		legalMoves = MoveGenerator.generateStrictlyLegal(position);
	}
	public void updateDisplay() {
		Icon icon;
		for (int i = 0; i < 64; i++) {
			int jpanelPos = littleEndianToJPanel(i);
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
