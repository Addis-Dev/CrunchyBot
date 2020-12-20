package main.java.anime;

import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// {
//         Media(search:"Naruto",type:ANIME){
//         title{
//         english
//         }
//         description
//         episodes
//         meanScore
//         coverImage{
//         medium
//         }
//         nextAiringEpisode{
//         timeUntilAiring
//         }
//         }
//         }

public class Anime {
    private static MockMvc mockMvc;
    @Autowired
    public static HttpURLConnection connection;
    public static HttpClient client = HttpClient.newHttpClient();

    public static String response = "Not Set Yet";
    public static String language = "en";
    public static String[] mainPath = {"attributes", "titles", "canonicalTitle"};
    public static String[] defaultPath = {"attributes", "titles", "canonicalTitle"}; ///D ONT CHANGE

    public static void getByHttp(String link, float a) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Anime::noParse)
                .join();
        mainPath = defaultPath;
    }

    public static void getByGraphql() throws Exception {
        System.out.println("Getting graph");
        String link = "https://graphql.anilist.co";
        mockMvc.perform(MockMvcRequestBuilders.post(link)
                .content("{\"query\":\"{ data { status hint } }\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        System.out.println("Done");

    }

    public static void setLanguage(String language1) {
        language = language1;
    }

    public static String parse(String res) {
        String name = "No Name";
        if (res.contains("{\"data\":[{\"id\":")) {
            System.out.println("Is an array");

            JSONObject json = new JSONObject(res);
            JSONArray data = json.getJSONArray("data");
            JSONObject first = data.getJSONObject(0);
            for (int i = 0; i < mainPath.length - 1; i++) {
                System.out.println("main path is" + mainPath[i]);
                first = first.getJSONObject(mainPath[i]);
                System.out.println("foorloop");
            }
            System.out.println("foorloop done");

            name = first.getString(mainPath[mainPath.length - 1]);
            response = name;

            System.out.println("Name is :" + name);
        } else {
            System.out.println("Is not an array");

            JSONObject first = new JSONObject(res);
            for (int i = 0; i < mainPath.length - 1; i++) {
                System.out.println("main path is" + mainPath[i]);
                if (mainPath[i].equalsIgnoreCase("en")) {

                }
                first = first.getJSONObject(mainPath[i]);
            }
            name = first.getString(mainPath[mainPath.length - 1]);

            response = name;
        }
        System.out.println("Parsed:" + name);
        return name;
    }

    public static String fullParse(String res) {
        String name = "No Name";
        for (int i = 0; i < mainPath.length; i++) {
            System.out.println(mainPath[i]);
        }
        if (res.contains("{\"data\":[{\"id\":")) {
            System.out.println("Is an array");
            JSONArray arr = new JSONArray(res);

            JSONObject first = arr.getJSONObject(0);
            for (int i = 0; i < mainPath.length - 1; i++) {
                System.out.println("main path is" + mainPath[i]);
                first = first.getJSONObject(mainPath[i]);
            }
            name = first.getString(mainPath[mainPath.length - 1]);
        } else {
            System.out.println("Is not an array");

            JSONObject first = new JSONObject(res);
            first = first.getJSONObject("data");
            for (int i = 0; i < mainPath.length - 1; i++) {
                System.out.println("main path is" + mainPath[i]);
                first = first.getJSONObject(mainPath[i]);
            }
            name = first.getString(mainPath[mainPath.length - 1]);
            response = name;
        }
        System.out.println("Parsed:" + name);
        return name;
    }

    public static String noParse(String res) {
        response = res;

        return response;
    }
}
