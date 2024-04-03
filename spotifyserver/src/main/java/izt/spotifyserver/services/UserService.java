package izt.spotifyserver.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import izt.spotifyserver.models.User;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

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
    
}
