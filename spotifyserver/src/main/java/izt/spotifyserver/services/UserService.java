package izt.spotifyserver.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.models.Artist;
import izt.spotifyserver.models.User;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

@Service
public class UserService {

    @Autowired
    private UserSQLRepository userSqlRepo;

    @Autowired
    private Neo4JUserService neo4jUserService;

    private final String PLACEHOLDER_IMAGE="https://t3.ftcdn.net/jpg/05/16/27/58/360_F_516275801_f3Fsp17x6HQK0xQgDQEELoTuERO4SsWV.jpg";


    public String getFriends(String username, int skip){
        List<String> usernames = neo4jUserService.findFriends(username, skip);
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        for(String u:usernames){
            SqlRowSet rowSet = userSqlRepo.getUsernameNameImagebyUsername(u);
            while(rowSet.next()){
                String friendUsername = rowSet.getString("username");
                String firstName = rowSet.getString("firstname");
                String lastName = rowSet.getString("lastname");
                String image = PLACEHOLDER_IMAGE;
                if(rowSet.getString("image")!=null){
                    image = rowSet.getString("image");
                }
                User user = new User();
                user.setUsername(friendUsername);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setImageUrl(image);
                JsonObject userJson = usernameNameImageJson(user);
                JAB.add(userJson);
            }
        }
        return JAB.build().toString();
    }

    public JsonObject usernameNameImageJson(User user){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("username", user.getUsername())
            .add("firstName",user.getFirstName())
            .add("lastName",user.getLastName())
            .add("image",user.getImageUrl());
        return JOB.build();
    }

    @Transactional("transactionManager")
    public void updateUserArtistsManually(String requestBody){
        JsonObject payload = Utils.stringToJson(requestBody);
        String username = payload.getString("username");
        JsonArray artists = payload.getJsonArray("artists");

        List<Artist> artistsToAdd = new ArrayList<>();
        List<String> genresRaw = new ArrayList<>();
        for(JsonValue a: artists){
            JsonObject artistJson = a.asJsonObject();
            String url = artistJson.getString("external_url");
            String image = artistJson.getString("image");
            String name = artistJson.getString("name");
            Artist artist = new Artist();
            artist.setImage(image);
            artist.setName(name);
            artist.setUsername(username);
            artist.setUrl(url);
            artistsToAdd.add(artist);
            JsonArray genresJson = artistJson.getJsonArray("genres");
            for(JsonValue g:genresJson){
                genresRaw.add(g.toString());
            }
        }
        List<String> sortedGenres = processGenres(genresRaw);
        userSqlRepo.deleteGenres(username);
        userSqlRepo.deleteArtists(username);
        int artistCount = 0;
        long genreCount = 0;
        for(Artist a: artistsToAdd){
            artistCount += userSqlRepo.addArtist(a);
        }
        for(String g: sortedGenres){
            genreCount += userSqlRepo.addGenres(g, username);
        }
        if(artistCount < artistsToAdd.size() || genreCount < sortedGenres.size()){
            throw new SQLFailedException();
        }
    }

    public List<String> processGenres(List<String> genresRaw){
        Map<String,Integer> genresAsMap = new HashMap<>();
        for(String s:genresRaw){
            if(genresAsMap.containsKey(s)){
                int count = genresAsMap.get(s);
                count = count+1;
                genresAsMap.replace(s,count);
            }
            else{
                int count = 1;
                genresAsMap.put(s,count);
            }
        }
        Map<String, Integer> sortedGenresMap = genresAsMap.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
        .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);

        Set<String> keys = sortedGenresMap.keySet();
        int length = 10;
        if(keys.size()<length){
            length = keys.size();
        }

        int counter = 0;

        List<String> topTenGenres = new ArrayList<String>();
        for(String s:keys){
            s = s.replace("\"","");
            topTenGenres.add(s);
            counter++;
            if(counter == length){
                break;
            }
        }
        return topTenGenres;

            
    }

    public String getUsersFromList(List<String> usernames){
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        for(String s: usernames){
            SqlRowSet rowset = userSqlRepo.getUsernameNameImagebyUsername(s);
            while(rowset.next()){
                String username = rowset.getString("username");
                String firstName = rowset.getString("firstname");
                String lastName = rowset.getString("lastname");
                String image = PLACEHOLDER_IMAGE;
                if(rowset.getString("image")!=null){
                    image = rowset.getString("image");
                }
            
                JsonObjectBuilder JOB = Json.createObjectBuilder();
                JOB.add("username",username)
                    .add("firstName", firstName)
                    .add("lastName",lastName)
                    .add("image",image);
                JAB.add(JOB.build());
            }
        }
        return JAB.build().toString();

    }
    
}
