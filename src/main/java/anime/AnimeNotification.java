package main.java.anime;


import main.java.firstbot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Text;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    public ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);

    TimerTask checkDate = new CheckDate(this,eb);
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
            timer.schedule(checkDate,10, TimeUnit.SECONDS); // every minute
        }
        membersList.add(member);
        startPing = true;
    }
    public void removeMember(Member member){
        membersList.remove(member);
        if(membersList.size() == 0){
            startPing = false;
            checkDate.cancel();
           timer.shutdown();
        }
    }
    public void setRelease(LocalDateTime release){
        this.release = release;
    }
}

class CheckDate extends TimerTask{
    EmbedBuilder eb;
    AnimeNotification notification;
    public CheckDate(AnimeNotification notification,EmbedBuilder eb){
        this.eb = eb;
        this.notification = notification;
        System.out.println("start");
    }

    @Override
    public void run() {
        if(notification.startPing) {
            System.out.println("notification date:" + notification.release.toLocalDate().getDayOfWeek());
            System.out.println("now date:" + LocalDate.now().getDayOfWeek());

            if (notification.release.toLocalDate().getDayOfWeek().equals(LocalDate.now().getDayOfWeek())) {
                System.out.println("This day");
                TimerTask task = new CheckHour(notification, eb);
                ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
                timer.schedule(task,10,TimeUnit.SECONDS);
                this.cancel();
            }
        }
    }
}

class CheckHour extends TimerTask{
    EmbedBuilder eb;
    AnimeNotification notification;
    public CheckHour(AnimeNotification notification,EmbedBuilder eb){
        this.eb = eb;
        this.notification = notification;
    }

    @Override
    public void run() {
        if( notification.release.toLocalTime().getHour() == LocalTime.now().getHour()){
           System.out.println("This hour");
            TimerTask task = new CheckMin(notification,eb);
            ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
            timer.schedule(task,10,TimeUnit.SECONDS);
            this.cancel();

        }
    }
}
class CheckMin extends TimerTask{
    EmbedBuilder eb;
    AnimeNotification notification;
    public CheckMin(AnimeNotification notification,EmbedBuilder eb){
        this.eb = eb;
        this.notification = notification;
    }
    @Override
    public void run() {
        if(notification.release.toLocalTime().getMinute() == LocalTime.now().getMinute()){
            System.out.println("This Min");
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
}
