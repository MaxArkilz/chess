package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return squares[position.getRow() - 1][position.getColumn() - 1];

    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < squares.length; i++ ) {
            Arrays.fill(squares[i], null);

        }
        for (int i = 0; i < squares.length; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        ChessPiece.PieceType[] backRow = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };
        for (int i = 0; i <= 7; i++){
            squares[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, backRow[i]);
            squares[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK,backRow[i]);
        }

    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * toString method generated using Claude Sonnet for easier debugging using following prompt:
     * "generate a toString method that returns pieces in the following format: r:[8,1], b:[8,2], ... , B:[1,7], R:[1,8]."
     * generalized code was provided and edited to fit within ChessBoard
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[row].length; col++) {
                ChessPiece piece = squares[row][col];
                if (piece != null) {
                    char symbol = piece.getPieceType().toString().charAt(0);
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        symbol = Character.toLowerCase(symbol);
                    } else {
                        symbol = Character.toUpperCase(symbol);
                    }
                    // Append piece and 1-based position
                    sb.append(symbol)
                            .append(":[").append(row + 1).append(",").append(col + 1).append("], ");
                }
            }
        }
        // Remove trailing comma and space
        if (sb.length() >= 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

}
