package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessGame() {
        this.currentTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {this.currentTurn = team;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTurn == chessGame.currentTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTurn, board);
    }


    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || board == null) {return null;}

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board,startPosition);
        List<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves){
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece movedPiece = board.getPiece(startPos);
            ChessPiece capturedPiece = board.getPiece(endPos);

            board.addPiece(endPos,movedPiece);
            board.addPiece(startPos,null);

            if (move.getPromotionPiece() != null){
                board.addPiece(endPos, null);
                board.addPiece(endPos,new ChessPiece(movedPiece.getTeamColor(),move.getPromotionPiece()));
            }

            if (!isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }

            board.addPiece(startPos,movedPiece);
            board.addPiece(endPos,capturedPiece);
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moves = validMoves(move.getStartPosition());

        if (moves == null || !moves.contains(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece movedPiece = board.getPiece(move.getStartPosition());

        if (movedPiece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException();
        }

            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();

            // make moves
            board.addPiece(endPos, movedPiece);
            board.addPiece(startPos, null);

            // promotion logic
            if (move.getPromotionPiece() != null) {
                board.addPiece(endPos, new ChessPiece(movedPiece.getTeamColor(), move.getPromotionPiece()));
            }
        // switch turn
        currentTurn = (currentTurn == TeamColor.WHITE)? TeamColor.BLACK:TeamColor.WHITE;

    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        TeamColor opponent = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // Iterate board positions
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // skip empty squares or pieces on current team
                if (piece == null || piece.getTeamColor() != opponent) {}

                if (canCaptureKing(piece, pos, kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canCaptureKing(ChessPiece piece, ChessPosition fromPos, ChessPosition kingPos) {
        for (ChessMove move : piece.pieceMoves(board, fromPos)) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        if (!isInCheck(teamColor)) {
            return false;
        }

        return boardIterate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        if (isInCheck(teamColor)) {
            return false;
        }

        return boardIterate(teamColor);
    }

    private boolean boardIterate(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    // return king current location for given team
    public ChessPosition findKing(ChessGame.TeamColor color) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null &&
                    piece.getTeamColor() == color &&
                    piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
            }
        }
    }
        throw new RuntimeException("The proletariat have risen up.");
}
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }



    private TeamColor currentTurn;
    private ChessBoard board;
}
