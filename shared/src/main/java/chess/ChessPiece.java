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

    public ChessPiece copy() {
        return new ChessPiece(this.pieceColor,this.type);
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

        List<ChessMove> moves = new ArrayList<>();
        switch (this.type) {
            case BISHOP -> slidingMoves(board, myPosition, moves, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case KING ->
                    stepMoves(board, myPosition, moves, new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}});
            case KNIGHT ->
                    stepMoves(board, myPosition, moves, new int[][]{{2, 1}, {2, -1}, {1, -2}, {1, 2}, {-1, -2}, {-1, 2}, {-2, -1}, {-2, 1}});
            case PAWN -> pawnMoves(board,myPosition,moves);
            case QUEEN ->
                    slidingMoves(board, myPosition, moves, new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}});
            case ROOK -> slidingMoves(board, myPosition, moves, new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}});

        }
        return moves;
    }

    private void pawnPromo(ChessPosition myPosition, List<ChessMove> moves, int promoRow, int row, ChessPosition newPos) {
        if (row == promoRow) {
            moves.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, newPos, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
        } else {
            moves.add(new ChessMove(myPosition, newPos, null));
        }
    }

    private void slidingMoves (ChessBoard board, ChessPosition start, List<ChessMove> moves, int[][] directions) {
        for (int[] dir:directions) {
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];

            while (insideBoard(row,col)) {
                ChessPosition newPos = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPos);

                if (target == null) {
                    moves.add(new ChessMove(start, newPos, null));
                } else {
                    if (target.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(start, newPos,null));
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
    }

    private  void stepMoves (ChessBoard board, ChessPosition start, List<ChessMove> moves, int[][] directions) {
        for (int[] dir:directions) {
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];

            if (insideBoard(row, col)) {
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPos);

                if (target == null || target.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(start,newPos, null));
                }
            }
        }
    }

    private void pawnMoves (ChessBoard board, ChessPosition start, List<ChessMove> moves){
        int facing = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 2:7;
        int promoRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 8:1;

        int row = start.getRow();
        int col = start.getColumn();

        if (insideBoard(row + facing, col) && board.getPiece(new ChessPosition(row + facing,col)) == null) {
            pawnPromo(start,moves,promoRow,row + facing,new ChessPosition(row + facing,col));
            if (start.getRow() == startRow) {
                int jump = start.getRow() + (2 * facing);
                if (board.getPiece(new ChessPosition(jump,col)) == null) {
                    moves.add(new ChessMove(start, new ChessPosition(jump, col), null));
                }
            }
        }

        for (int i: new int[] {-1,1}) {
            if (insideBoard(row + facing, col + i) && board.getPiece(new ChessPosition(row + facing, col + i)) != null) {
                if (this.pieceColor != board.getPiece(new ChessPosition(row + facing, col + i)).getTeamColor()) {
                    pawnPromo(start,moves,promoRow,row + facing, new ChessPosition(row + facing, col + i));
                }
            }
        }
    }

    private boolean insideBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}