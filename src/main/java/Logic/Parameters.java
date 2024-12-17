package Logic;

public class Parameters {
    private static int boardSize=8;
    private static int screenWidth = 1000;
    private static int screenHeight = 700;

    public static int boardSize() {
        return boardSize;
    }

    public static void setBoardSize(int boardSize) {
        Parameters.boardSize = boardSize;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static void setScreenWidth(int screenWidth) {
        Parameters.screenWidth = screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static void setScreenHeight(int screenHeight) {
        Parameters.screenHeight = screenHeight;
    }
}
