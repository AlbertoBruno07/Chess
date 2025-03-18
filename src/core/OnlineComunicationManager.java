package core;

import gui.GameFrame.BoardPanel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class OnlineComunicationManager implements Runnable{
    private String url;
    private int port, game;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Board board;
    private Move nextOppenentMove;
    private BoardPanel boardPanel;

    public OnlineComunicationManager(String url, int port, int game) {
        this.url = url;
        this.port = port;
        this.game = game;
    }

    public int makeSocket(){
        try {
            socket = new Socket(url, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            outputStream.write(game);
            return inputStream.read();
        } catch (IOException e) {
            return -1;
        }
    }

    public void closeSocket(){
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMove(Move move){
        try {
            outputStream.write(move.getSourceRow());
            outputStream.write(move.getSourceColumns());
            outputStream.write(move.getTargetRow());
            outputStream.write(move.getTargetColumns());
            if(move.getPromotedPieceType() != null) {
                outputStream.write(1);
                sendPromotedPieceType(move.getPromotedPieceType());
            }
            else
                outputStream.write(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Move getMove(){
        int sR, sC, tR, tC;

        try {
            sR = inputStream.read();
            sC = inputStream.read();
            tR = inputStream.read();
            tC = inputStream.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Move(sR, sC, tR, tC, board);
    }

    public Move getNextOppenentMove() {
        return nextOppenentMove;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    @Override
    public void run() {
        nextOppenentMove = getMove();
        boardPanel.processOnlineOpponentMove(nextOppenentMove);
    }

    //To be reviewed
    public int getStart() {
        try {
            return inputStream.read();
        } catch (IOException e) {
            return -1;
        }
    }

    public String getPromotedPieceType() {
        int pieceType;
        try {
            pieceType = inputStream.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return switch (pieceType){
            case 0 -> "Bishop";
            case 1 -> "Rook";
            case 2 -> "Queen";
            case 3 -> "Knight";
            case 4 -> "Pawn";
            default -> throw new IllegalStateException("[CommManager] Unexpected value: " + pieceType);
        };
    }

    public void sendPromotedPieceType(String pieceType) {
        try {
            outputStream.write(switch (pieceType){
                case "Bishop" -> 0;
                case "Rook"   -> 1;
                case "Queen"  -> 2;
                case "Knight" -> 3;
                case "Pawn"   -> 4;
                default -> throw new IllegalStateException("Unexpected value: " + pieceType);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
