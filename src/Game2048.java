import com.javarush.engine.cell.*;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void createGame(){
        gameField = new int[SIDE][SIDE];
        for(int i = 0; i < 2; i++) {
            createNewNumber();
        }
    }

    private void drawScene() {
        for(int i = 0; i < SIDE; i++) {
            for(int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }
    }

    private void createNewNumber() {
        if(getMaxTileValue() < 2048) {
            int x = getRandomNumber(0, SIDE);
            int y = getRandomNumber(0, SIDE);
            if (gameField[x][y] != 0) {
                createNewNumber();
            } else {
                int cellValue = getRandomNumber(10);
                if (cellValue == 9)
                    gameField[x][y] = 4;
                else
                    gameField[x][y] = 2;
            }
        } else {
            win();
        }
    }

    private void setCellColoredNumber(int x, int y, int value) {
        if(value!= 0)
            setCellValueEx(x, y, getColorByValue(value), String.valueOf(value));
        else
            setCellValueEx(x, y, getColorByValue(value), "");
    }

    private Color getColorByValue(int value) {
        switch (value) {
            case 2 : return Color.BLUE;
            case 4 : return Color.GREEN;
            case 8 : return Color.YELLOWGREEN;
            case 16 : return Color.AQUAMARINE;
            case 32 : return Color.PINK;
            case 64 : return Color.PURPLE;
            case 128 : return Color.RED;
            case 256 : return Color.BEIGE;
            case 512 : return Color.BROWN;
            case 1024 : return Color.SILVER;
            case 2048 : return Color.GOLD;
            default: return Color.WHITE;
        }
    }

    private boolean compressRow(int[] row) {
        boolean isChanged = false;
        for(int i = 0; i < row.length; i++) {
            if(row[i] == 0 ) {
                for(int j = i; j < row.length ; j++) {
                    if(row[j] !=0) {
                        int temp = row[i];
                        row[i] = row[j];
                        row[j] = temp;
                        isChanged = true;
                        break;
                    }
                }
            }
        }
        return isChanged;
    }

    private boolean mergeRow(int[] row) {
        boolean isMerged = false;
        for(int i  = 0; i < row.length-1; i++) {
            if(row[i] !=0) {
                if(row[i] == row[i+1]) {
                    row[i] = row[i] + row[i+1];
                    row[i+1] = 0;
                    isMerged = true;
                    score = score + row[i];
                    setScore(score);
                }
            }
        }
        return isMerged;
    }

    @Override
    public void onKeyPress(Key key) {

        if(isGameStopped && key == Key.SPACE) {
            isGameStopped = false;
            score = 0;
            setScore(score);
            createGame();
            drawScene();
        }
        if(!isGameStopped) {
            if (canUserMove()) {
                if (key == Key.LEFT) {
                    moveLeft();
                    drawScene();
                } else if (key == Key.DOWN) {
                    moveDown();
                    drawScene();
                } else if (key == Key.RIGHT) {
                    moveRight();
                    drawScene();
                } else if (key == Key.UP) {
                    moveUp();
                    drawScene();
                }
            } else {
                gameOver();
            }
        }
    }

    private void moveLeft(){
        boolean isChanged = false;
        for(int i = 0; i < SIDE; i++) {
            if(compressRow(gameField[i]))
                isChanged = true;
            if(mergeRow(gameField[i]))
                isChanged = true;
            if(compressRow(gameField[i]))
                isChanged = true;
        }
        if (isChanged)
            createNewNumber();
    }

    private void moveRight(){
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveDown(){
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp(){
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void rotateClockwise() {
        int[][] tempArray = new int[SIDE][SIDE];
        for(int i = 0; i < tempArray.length; i++) {
            for(int j = 0; j < tempArray.length; j++) {
                tempArray[j][SIDE-i-1] = gameField[i][j];
            }
        }
        gameField = tempArray;
    }

    private int getMaxTileValue() {
        int maxValue = 0;
        for(int i = 0; i < SIDE; i++) {
            for(int j = 0; j < SIDE; j++) {
                if(maxValue < gameField[i][j])
                    maxValue = gameField[i][j];
            }
        }
        return maxValue;
    }

    private void win() {
        showMessageDialog(Color.GOLD, "Вы выиграли!", Color.BLACK, 24);
        isGameStopped = true;
    }

    private boolean canUserMove() {
        boolean canMove = false;
        for(int i = 0; i < SIDE; i++) {
            for(int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0)
                    canMove = true;
                else if(j > 1 && gameField[i][j] == gameField[i][j-1])
                    canMove = true;
                else if (j < 3 && gameField[i][j] == gameField[i][j+1])
                    canMove = true;
                else if (i > 1 && gameField[i][j] == gameField[i-1][j])
                    canMove = true;
                else if(i < 3 && gameField[i][j] == gameField[i+1][j])
                    canMove = true;
            }
        }
        return canMove;
    }

    private void gameOver() {
        showMessageDialog(Color.RED, "Вы проиграли!", Color.BLACK, 24);
        isGameStopped = true;
    }
}
