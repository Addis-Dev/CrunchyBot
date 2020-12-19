package main.java.firstbot;

import Snake.SnakeGame;
import main.Config;
import main.java.anime.AnimeNotification;
import main.java.events.onMessageReceived;
import main.java.events.onReactionReceived;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;



public class Bot {

    public static final List<SnakeGame> snakeGames = new ArrayList<>();
    public static final List<AnimeNotification> animeNotification = new ArrayList<>();

    public static void main(String[] args) throws Exception{
        JDA api = JDABuilder.createDefault(Config.get("TOKEN")).build();
        onReactionReceived onReactionReceived = new onReactionReceived();
        api.addEventListener(new onMessageReceived());
        api.addEventListener(onReactionReceived);
    }

    public static void addGame(TextChannel channel, int size){
        SnakeGame game = new SnakeGame(channel,size);
        snakeGames.add(game);
    }
    public static List<SnakeGame> getSnakeGames(){
        return snakeGames;
    }
    public static List<AnimeNotification> getAnimeNotification(){
        return animeNotification;
    }
    //public void addGame(OtherGame game){
    //    otherGames.add(game);
    //}
}
//