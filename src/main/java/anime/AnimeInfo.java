package main.java.anime;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONObject;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class AnimeInfo {

    String crunchyRoll = "https://www.crunchyroll.com/";

    String json;
    TextChannel channel;

    String title;
    String description;
    public String thumbnail;
    String episodes;
    String rating;
    public String slug;
    Boolean completeSeries;
    public LocalDateTime nextEp = LocalDateTime.of(LocalDate.now(),LocalTime.of(LocalTime.now().getHour(), 44));

    EmbedBuilder eb = new EmbedBuilder();
    StringBuilder stars = new StringBuilder();
    Message message;
    public Message thisMessage;
    public String messageId;

    public AnimeInfo(TextChannel channel, String json,Message msg){
        this.json = json;
        this.channel = channel;
        this.message = msg;
    }

    public void parse(){
        String name = "Title";
        String des = "Description...";
        String pic = "https://media.kitsu.io/anime/poster_images/42765/medium.jpg";
        String eps = "220";
        String stars = "5";

        if(json.contains("{\"data\":[{\"id\":")){
            JSONObject jsn = new JSONObject(json);
            JSONObject first = jsn.getJSONArray("data").getJSONObject(0);
            name = first.getJSONObject("attributes").getString("canonicalTitle");//data.attributes.canonicalTitle
            des = first.getJSONObject("attributes").getString("description");//data.attributes.description
            pic = first.getJSONObject("attributes").getJSONObject("posterImage").getString("medium");//data.attributes.posterImage.medium
            if(!first.getJSONObject("attributes").isNull("episodeCount")) {
                eps = String.valueOf(first.getJSONObject("attributes").getInt("episodeCount"));//data.attributes.episodeCount
            }
            stars = String.valueOf((int)first.getJSONObject("attributes").getFloat("averageRating")/25);//data.attributes.averageRating

            slug = first.getJSONObject("attributes").getString("slug");
            crunchyRoll += slug;

            completeSeries = first.getJSONObject("attributes").getString("status").equalsIgnoreCase("finished");
            System.out.println(name + " "+ eps + " " + stars);
        }
        title = name;
        description = des;
        thumbnail = pic;
        episodes = eps;
        rating = stars;
    }

    public void showInfo(){
        eb.clear();
        eb.setTitle(title ,crunchyRoll);
        String[] words = description.split(" ");
        StringBuilder descript = new StringBuilder();
        for(int i = 0; i < 6; i++){
            descript.append(" ").append(words[i]);
        }
        descript.append("...");
        eb.setDescription(descript.toString());
        eb.setColor(Color.GREEN);
        eb.setThumbnail(thumbnail);
        eb.addField("Episodes:", String.valueOf(episodes), false);

        stars = new StringBuilder();
        stars.append("⭐".repeat(Math.max(0, Integer.parseInt(rating))));

        eb.addField("Rating:", stars.toString(), false);
        if(!completeSeries) {
            if (nextEp.toLocalDate().equals(LocalDate.now())) {
                eb.setFooter("Next Episode: " + nextEp.toLocalTime());
            } else {
                eb.setFooter("Next Episode: " + nextEp.toLocalDate());
            }
            channel.sendMessage(eb.build()).queue(message -> {
                thisMessage = message;
                messageId = message.getId();

                System.out.println("message sent is:" + messageId);

                message.addReaction("U+1F514").queue();
                message.addReaction("U+1F515").queue();
                message.delete().queueAfter(5, TimeUnit.MINUTES);
            });
        }else{
            eb.setFooter("Complete Series");
            channel.sendMessage(eb.build()).queue(message -> {
                thisMessage = message;
                messageId = message.getId();
            });
        }

        channel.deleteMessageById(message.getId()).queue();
    }
    public String getName(){
        return title;
    }
}
