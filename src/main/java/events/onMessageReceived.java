package main.java.events;

import main.Config;
import main.java.anime.Anime;
import main.java.anime.AnimeInfo;
import main.java.anime.AnimeNotification;
import main.java.firstbot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class onMessageReceived extends ListenerAdapter {
    EmbedBuilder eb = new EmbedBuilder();
    Message msg;
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        String pfx = Config.get("PREFIX");

        String messageSent = e.getMessage().getContentRaw();
        String[] args = messageSent.split(" ");

        if (messageSent.equalsIgnoreCase(pfx+"play")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Games:");
            eb.setDescription("Snake");
            eb.setColor(Color.BLUE);
            e.getChannel().sendMessage(eb.build()).queue();
        }
        if (messageSent.equalsIgnoreCase(pfx+"play snake")) {
            Bot.addGame(e.getChannel(), 10);
        }
        if (messageSent.equalsIgnoreCase(pfx+"p snake")) {
            Bot.addGame(e.getChannel(), 10);
        }
        if (messageSent.equalsIgnoreCase(pfx+"snake")) {
            Bot.addGame(e.getChannel(), 10);
        }
        if (args[0].equalsIgnoreCase(pfx+"setlanguage")) {
            if (args.length == 1) {
                e.getChannel().sendMessage("You need to add a language [en, ja]").queue();
            }
            else {
                if (args[1].equalsIgnoreCase("english") || args[1].equalsIgnoreCase("en") || args[1].equalsIgnoreCase("eng")) {
                    Anime.setLanguage("en_jp");
                    e.getChannel().sendMessage("Set Language to English").queue();
                } else if (args[1].equalsIgnoreCase("japanese") || args[1].equalsIgnoreCase("ja") || args[1].equalsIgnoreCase("jp"))  {
                    Anime.setLanguage("ja_jp");
                    e.getChannel().sendMessage("Set Language to Japanese").queue();
                }
                else{
                    e.getChannel().sendMessage("Invalid Language [en, ja]").queue();
                }
            }
        }
        if (args[0].equalsIgnoreCase(pfx+"a")||args[0].equalsIgnoreCase(pfx+"anime")) {
            if (args.length == 1) {
                e.getChannel().sendMessage("Add the name of the anime you would like to search for").queue();
                return;
            } else {
                ///anime?filter[text]=cowboy%20bebop
                StringBuilder str = new StringBuilder();
                str.append("https://kitsu.io/api/edge/anime/?filter[text]=");
                for (int i = 1; i < args.length; i++) {
                    str.append(args[i]);
                    if (i != args.length - 1) {
                        str.append("%20");
                    }
                }

                e.getChannel().sendMessage("Searching...").queue(message -> {
                    msg = message;
                });
                Anime.getByHttp(str.toString(),1.5f);
                AnimeInfo info = new AnimeInfo(e.getChannel(),Anime.response, msg);

                info.parse();
                info.showInfo();

                AnimeNotification notification =
                        new AnimeNotification(info.getName(),info.slug,info.thumbnail,
                                LocalDateTime.of(LocalDate.of(2020,12,25), LocalTime.of(12,0)),e.getChannel());
                while(notification.getMessageId() ==  null) {
                    notification.setMessageId(info.messageId);
                }
                Bot.animeNotification.add(notification);

            }
        }
        if (messageSent.equalsIgnoreCase(pfx+"auth")) {
            Anime.getAccessToken();
            String txt = Anime.response;
            e.getChannel().sendMessage("Auth Token: " + txt).queue();
            System.out.println(txt);
        }
        if(messageSent.equalsIgnoreCase(pfx+"test")){
            eb.clear();
            eb.setTitle("Jujutsu Kaisen");
            eb.setDescription("Mahito corners Nanami, but he releases...");
            eb.setColor(0x5dea79);
            eb.setThumbnail("https://media.kitsu.io/anime/poster_images/42765/medium.jpg");
            eb.addField("Episodes:", "11", false);
            eb.addField("Rating:", "⭐⭐⭐⭐⭐", false);
            eb.setFooter("Next Episode: 2h");
            e.getChannel().sendMessage(eb.build()).queue(message -> {
                message.addReaction("U+1F514").queue();
                message.addReaction("U+1F515").queue();

            });
        }

    }
}
