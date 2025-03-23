package onlineComunicationManager;

import core.Board;
import core.Move;
import gui.gameFrame.BoardPanel;

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
            System.out.println("Sending move");
            outputStream.write(move.getSourceRow());
            outputStream.write(move.getSourceColumns());
            outputStream.write(move.getTargetRow());
            outputStream.write(move.getTargetColumns());
            System.out.println("Move sent");
            if(move.getPromotedPieceType() != null) {
                System.out.println("Writing one");
                outputStream.write(1);
                System.out.println("Wrote one");
                sendPromotedPieceType(move.getPromotedPieceType());
            }
            else{
                System.out.println("Writing zero");
                outputStream.write(0);
                System.out.println("Wrote zero");
            }
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
            System.out.println("Getting move");
            sR = inputStream.read();
            System.out.println(sR);
            sC = inputStream.read();
            System.out.println(sC);
            tR = inputStream.read();
            System.out.println(tR);
            tC = inputStream.read();
            System.out.println(tC);
            System.out.println("Move gotten");
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
        boardPanel.processExternalMove(nextOppenentMove);
        Thread.currentThread().interrupt();
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
            System.out.println("Getting promoted piece type");
            pieceType = inputStream.read();
            System.out.println("Promoted piece type gotten");
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

    public int getTime() {
        int res = 0;
        try {
            res += inputStream.read();
            res += inputStream.read() << 8;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public void sendTime(int time) {
        try {
            outputStream.write(time);
            outputStream.write(time >> 8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
