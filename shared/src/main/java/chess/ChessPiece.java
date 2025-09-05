package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();

        if (piece.getPieceType() == PieceType.BISHOP) {
            // for each direction (row, col):
            //      newRow = currentRow + row
            //      newCol = currentCol + col
            //      while position is inside board:
            //          if empty -> add move
            //          if opponent -> add move + stop
            //          if ally -> stop
            //      move in that direction
            int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}};

            for(int[] dir : directions) {
                int row = myPosition.getRow() + dir[0];
                int col = myPosition.getColumn() + dir[1];

                while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                    ChessPosition newPos = new ChessPosition(row, col);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        if (target.getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition,newPos,null));
                        }
                        break;
                    }
                    row += dir[0];
                    col += dir[1];
                }

            }
        }
        if (piece.getPieceType() == PieceType.QUEEN){
        int[][] directions = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
            for(int[] dir : directions){
                int row = myPosition.getRow() + dir[0];
                int col = myPosition.getColumn() + dir[1];

                while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                    ChessPosition newPos = new ChessPosition(row, col);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition,newPos,null));
                    } else {
                        if (target.getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                    row += dir[0];
                    col += dir[1];
                }
            }
        }
        if (piece.getPieceType() == PieceType.ROOK){
            int[][] directions = {{1,0},{0,1},{-1,0},{0,-1}};
            for(int[] dir : directions){
                int row = myPosition.getRow() + dir[0];
                int col = myPosition.getColumn() + dir[1];

                while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                    ChessPosition newPos = new ChessPosition(row, col);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition,newPos,null));
                    } else {
                        if (target.getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                    row += dir[0];
                    col += dir[1];
                }
            }
        }
        if (piece.getPieceType() == PieceType.KING) {
            int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
            for (int[] dir : directions) {
                int row = myPosition.getRow() + dir[0];
                int col = myPosition.getColumn() + dir[1];

                if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                    ChessPosition newPos = new ChessPosition(row, col);
                    ChessPiece target = board.getPiece(newPos);
                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else {
                        if (target.getTeamColor() != piece.getTeamColor()) {
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                    }
                }
            }
        }
        if (piece.getPieceType() == PieceType.PAWN){
            //direction based on color (negate all row operations for black?)
            //starting row for double move
            //promotion row and logic
            //capture logic if enemy diagonal
            int[][] directions = {{1,0},{1,-1},{1,1}};
            int[] startRow = {2,0};
            int[] promoRow = {8,0};
            if (piece.getTeamColor() != ChessGame.TeamColor.WHITE) {
                int[][] blkDirections ={{-1,0},{-1,-1},{-1,1}};
                for (int [] dir : directions) {
                    directions[0] = blkDirections[0];
                }
                startRow[0] = 7;
                promoRow[0] = 1;
            }
            for (int[] dir : directions) {
                int row = myPosition.getRow() + dir[0];
                int col = myPosition.getColumn() + dir[1];

                if (row <= 8 && row >= 1 && col <= 8 && col >= 1) {
                    ChessPosition newPos = new ChessPosition(row, col);
                    ChessPiece target = board.getPiece(newPos);

                    //promotion
                    //capture
                    //starting leap
                }
            }

        }
        if (piece.getPieceType() == PieceType.KNIGHT){

        }
        return moves;
    }
}
