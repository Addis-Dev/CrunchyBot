package main.java.anime;


import main.java.firstbot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Text;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AnimeNotification {

    public List<Member> membersList = new ArrayList<>();
    String crunchyRoll = "https://www.crunchyroll.com/";
    Message message;
    TextChannel channel;
    String messageId;
    String name;
    LocalDateTime release;

    EmbedBuilder eb = new EmbedBuilder();

    Boolean startPing = false;
    TimerTask task = new MyTimer(this,eb);
    Timer timer = new Timer();
    public AnimeNotification(String name, LocalDateTime release, Message msg){
        this.name = name;
        this.release = release;
        this.message = msg;
    }
    public AnimeNotification(String name,String slug,String thumbnail, LocalDateTime release , TextChannel channel){
        this.name = name;
        this.release = release;
        this.channel = channel;
        eb.setTitle(name,crunchyRoll+slug);
        eb.setColor(Color.ORANGE);
        eb.setDescription("The Latest Episode of "+ name +" is Out Now!");
        eb.setThumbnail(thumbnail);


    }
    public void setMessage(Message message){
        this.message = message;

    }
    public void setMessageId(String messageId){
        this.messageId = messageId;
    }
    public String getMessageId(){
        return messageId;
    }
    public Message getMessage(){
        return message;
    }

    public void addMember(Member member){
        if(membersList.size() == 0){
            timer.schedule(task,1000,20000);
        }
        membersList.add(member);
        startPing = true;
    }
    public void removeMember(Member member){
        membersList.remove(member);
        if(membersList.size() == 0){
            startPing = false;
            task.cancel();
            timer.cancel();
        }
    }
    public void setRelease(LocalDateTime release){
        this.release = release;
    }

}

class MyTimer extends TimerTask{
    EmbedBuilder eb;
    AnimeNotification notification;
    public MyTimer(AnimeNotification notification,EmbedBuilder eb){
        this.eb = eb;
        this.notification = notification;
    }

    @Override
    public void run() {
        if(notification.startPing) {
            StringBuilder stringBuilder = new StringBuilder();
            for(Member member :notification.membersList) {
                stringBuilder.append(" ").append(member.getAsMention());
            }
            notification.channel.sendMessage(stringBuilder).queue(message -> {
                message.delete().queueAfter(1,TimeUnit.MILLISECONDS);
            });
            notification.channel.sendMessage(eb.build()).queue(message -> {
                message.delete().queueAfter(1, TimeUnit.MINUTES);
            });
        }
    }
}
