package main.java.events;

import main.java.anime.AnimeNotification;
import main.java.firstbot.Bot;
import Snake.SnakeGame;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class onReactionReceived extends ListenerAdapter {

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e){
        if (!e.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)){
            e.getChannel().sendMessage("Please give me Admin dickhead").queue();
            return;
        }
        System.out.println(e.getReaction());
        List<SnakeGame> snakeGameList = Bot.getSnakeGames();
        EmbedBuilder eb = new EmbedBuilder();

        if(!e.getUser().isBot()) {
            for (SnakeGame game : snakeGameList) {
                if (e.getMessageId().equals(game.getMessageId())) {
                    System.out.println("Snake");
                    game.getDirection(e.getReaction().toString());
                    e.getReaction().removeReaction(e.getUser()).queue();
                }
            }

            for(AnimeNotification notification: Bot.getAnimeNotification()){

                if(e.getMessageId().equalsIgnoreCase(notification.getMessageId())){
                    if(e.getReaction().toString().contains("U+1f514")) {
                        if(!notification.membersList.contains(e.getMember())) {
                            notification.addMember(e.getMember());
                            eb.clear();
                            eb.setColor(Color.GREEN);
                            eb.addField("You will now receive notifications from this anime.",
                                    "If you would like to stop receiving notifications press the :no_bell: icon, "+ e.getUser().getAsMention(),false);
                        }else{
                            eb.clear();
                            eb.setColor(Color.CYAN);
                            eb.addField("You are already receiving notifications from this anime.",
                                    "If you would like to stop receiving notifications press the :no_bell: icon, "+ e.getUser().getAsMention(),false);
                        }
                        e.getReaction().removeReaction(e.getUser()).queue();
                        //eb.setFooter("If you would like to stop receiving notifications press the :no_bell: icon");

                        e.getChannel().sendMessage(eb.build()).queue(message -> {
                            message.delete().queueAfter(6, TimeUnit.SECONDS);
                        });
                    }else if(e.getReaction().toString().contains("U+1f515")){
                        if(notification.membersList.contains(e.getMember())) {
                            notification.removeMember(e.getMember());
                            eb.clear();
                            eb.setColor(Color.RED);
                            eb.addField("You will now stop receiving notifications from this anime." ,
                                    "If you would like to receive notifications press the :bell: icon, "+ e.getUser().getAsMention(),false);
                        }else{
                            eb.clear();
                            eb.setColor(Color.CYAN);
                            eb.addField("You are not receiving notifications from this anime.",
                                    "If you would like to receive notifications press the :bell: icon, "+ e.getUser().getAsMention(),false);
                        }
                        e.getReaction().removeReaction(e.getUser()).queue();
                        //eb.addField("If you would like to receive notifications press the :bell: icon");
                        e.getChannel().sendMessage(eb.build()).queue(message -> {
                            message.delete().queueAfter(6, TimeUnit.SECONDS);
                        });
                    }
                }
            }
            System.out.println(Bot.getAnimeNotification());
        }
    }
}
