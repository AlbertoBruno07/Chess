package engine;

import core.Board;
import core.Move;
import gui.GameFrame.BoardPanel;

import java.io.*;
import java.util.Scanner;

public class Engine {

    private Process process;
    private BufferedWriter writer;
    private Scanner reader;

    private String positionCommand;
    private Board board;
    private BoardPanel boardPanel;
    private boolean firstMoveAppended;
    private String promotedPieceType;
    private char promotedPieceTypeChar;

    public Engine(Board board, BoardPanel bP) {
        this.board = board;
        this.boardPanel = bP;
        promotedPieceType = null;
        promotedPieceTypeChar = 'a';
        firstMoveAppended = false;
        positionCommand = "position startpos";

        ProcessBuilder pb = new ProcessBuilder("./src/engine/stockfish.exe");

        try {
            process = pb.start();
            reader = new Scanner(process.getInputStream());
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write("ucinewgame\n");
            writer.flush();
            System.out.println(reader.nextLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendMove(Move move){
        if(!firstMoveAppended){
            positionCommand += " moves";
            firstMoveAppended = true;
        }
        positionCommand += " " + move.toString();
        if(promotedPieceTypeChar != 'a') {
            positionCommand += promotedPieceTypeChar;
            promotedPieceTypeChar = 'a';
        }
    }

    private void sendCommand(String command){
        try {
            writer.write(command + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Move getMove(){
        sendCommand("ucinewgame");
        sendCommand(positionCommand);
        System.out.println(positionCommand);
        sendCommand("go movetime 10");
        String read = "";
        while(reader.hasNextLine()) {
            read = reader.nextLine();
            if(read.contains("bestmove")){
                System.out.println(read);
                break;
            }
        }
        read = read.replace("bestmove ", "");
        Move move = getMove(read);
        return move;
    }

    private Move getMove(String read) {
        int sR = 8-(read.charAt(1)-48),
        sC = (int) read.charAt(0)-97,
        tR = 8-(read.charAt(3)-48),
        tC = (int) read.charAt(2)-97;
        Move move = new Move(sR, sC, tR, tC, board);
        System.out.println(sR + " " + sC + " " + tR + " " + tC);
        if(read.length() > 4)
            if(read.charAt(4) != ' '){
                promotedPieceTypeChar = read.charAt(4);
                promotedPieceType = (switch (promotedPieceTypeChar){
                    case 'b' -> "Bishop";
                    case 'q' -> "Queen";
                    case 'r' -> "Rook";
                    case 'k' -> "Knight";
                    default -> throw new IllegalStateException("Unexpected value: " + read.charAt(4));
                });
            }
        return move;
    }

    public void makeMove() {
        boardPanel.processStockfishMove(getMove());
    }

    public String getPromotedPieceType() {
        String res = promotedPieceType;
        promotedPieceType = null;
        return res;
    }
}
