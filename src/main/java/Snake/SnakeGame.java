package Snake;

import main.java.firstbot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Random;


public class SnakeGame {

    int size;
    int x,y,fruitX,fruitY,score;
    boolean gameOver;

    int[] tailX = new int[100];
    int[] tailY= new int[100];
    int nTail = 1;

    String[][] gameBoard;
    String[][] blankBoard;

    TextChannel channel;
    Message message;
    String messageId;
    eDirection direction;
    EmbedBuilder eb = new EmbedBuilder();

    enum eDirection{
        STOP,
        LEFT,
        RIGHT,
        UP,
        DOWN
    };


    public SnakeGame(TextChannel channel,int size){
        this.channel = channel;
        this.size = size;

        setup();
    }

    void setup(){
        gameOver = false;
        direction = eDirection.STOP;
        x = 9;
        y = 9;
        moveFruit();
        score = 0;
        draw();
    }

    void draw(){

        gameBoard = createBoard(10, 10);
        blankBoard = createBoard(10, 10);

        //gameBoard[fruitX][fruitX] = ":apple:";
        gameBoard[x][y] = ":green_circle:";

        eb.setTitle("Snake");
        eb.setDescription(toString(gameBoard));
        eb.setColor(Color.GREEN);
        eb.setFooter("Score: "+score);
        channel.sendMessage(eb.build()).queue(message -> {
            message.addReaction("U+2B05").queue();
            message.addReaction("U+2B06").queue();
            message.addReaction("U+2B07").queue();
            message.addReaction("U+27A1").queue();
            this.message = message;
            messageId = message.getId();
        });
    }

    public void update() {

        if (!gameOver) {
            gameBoard = createBoard(size, size);
            gameBoard[fruitX][fruitY] = ":apple:";
            gameBoard[x][y] = ":green_circle:";
            if(nTail != 0) {
                for (int i = 0; i < nTail; i++) {
                    if (!(tailX[i] == x && tailY[i] == y)) {
                        if(!(tailX[i]== 0 && tailY[i] == 0)) {

                            gameBoard[tailX[i]][tailY[i]] = ":green_square:";
                        }
                    }
                }
            }
            //:green_circle:
            //:green_square:
            eb.setTitle("Snake, X:" + x + " Y:" + y);
            eb.setDescription(toString(gameBoard));
            eb.setFooter("Score: " + score);
            message.editMessage(eb.build()).queue();
            logic();
        }
    }

    void moveFruit(){
        Random rand = new Random();
        fruitX = rand.nextInt(getSize());
        rand = new Random();
        fruitY = rand.nextInt(getSize());
        if(fruitX == 0 || fruitY == 0 || fruitX == size || fruitY == size){
            moveFruit();
        }
        if(x == fruitX && y == fruitY) {
            moveFruit();
        }
    }

    void logic(){
        int prevX = tailX[0];
        int prevY = tailY[0];
        int prev2X,prev2Y;
        tailX[0] = x;
        tailY[0] = y;

        for(int i = 1; i < nTail; i ++){
            prev2X = tailX[i];
            prev2Y = tailY[i];

            tailX[i] = prevX;
            tailY[i] = prevY;

            prevX = prev2X;
            prevY = prev2Y;
        }

        if(x == 0 || y == 0 || x == size || y == size){
            gameOver = true;
            gameOver();
            return;
        }//check if snake hit edge

        for(int  i = 0; i < nTail; i++){
            if(tailX[i] == x && tailY[i] == y){

                System.out.println(gameBoard[x][y]);
            }
        }
        if(x == fruitX && y == fruitY){
            score += 1;
            moveFruit();
            nTail += 1;
            update();
        }
    }

    String[][] createBoard(int sizeX,int sizeY){
        String[][] strings = new String[sizeX+1][sizeY+1];
        for(int x = 0; x <= sizeX; x++){
            for(int y = 0; y <= sizeY; y++){
                if(x == 0 || y == 0 || x == sizeX || y == sizeY){
                    strings[x][y] = " ";
                }
                else {
                    strings[x][y] = ":black_large_square:";
                }
                //:blu e_square:
                //:black_large_square:
            }
        }
        return strings;
    }
    String toString(String[][] string){
        StringBuilder stringBuilder = new StringBuilder();
        for(int y = 0; y <string.length; y++) {
            for (int x = 0; x < string.length; x++) {
                stringBuilder.append(string[x][y]);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void getDirection(String emoji){

        if (emoji.contains("U+2b05")) {
            x--;
            update();
            return;
        } //left
        else if (emoji.contains("U+2b06")) {
            y--;
            update();
            return;
        } //up
        else if (emoji.contains("U+2b07")) {
            y++;
            update();
            return;
        } //down
        else if (emoji.contains("U+27a1")) {
            x++;
            update();
            return;
        } //right
        if (emoji.contains("U+1F501")) {
            System.out.println("Restart");
            Bot.addGame(channel, 10);
            Bot.snakeGames.remove(this);
        }//reset NOT WORKING YET
    }

    public void gameOver(){
        //gameBoard = createBoard(size,size);
        eb.setColor(Color.RED);
        eb.setTitle("GAME OVER!!");
        eb.setDescription(toString(gameBoard)+"\n"+"React with :repeat: to play again");
        message.clearReactions().queue();
        message.addReaction("U+1F501").queue();
        message.editMessage(eb.build()).queue();
        gameOver = true;
    }

    public String getMessageId(){
        return messageId;
    }
    public GuildChannel getChannel(){
        return channel;
    }

    public int getSize(){
        return size;
    }
    public int getScore(){
        return score;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getFruitX(){
        return fruitX;
    }
    public int getFruitY(){
        return fruitY;
    }
    public void setGameOver(boolean bool){
        gameOver = bool;
    }
}
