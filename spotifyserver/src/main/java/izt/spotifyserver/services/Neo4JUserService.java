package izt.spotifyserver.services;

import java.util.List;

import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.models.Instrument;
import izt.spotifyserver.models.Neo4jUser;
import izt.spotifyserver.repositories.InstrumentNeo4JRepository;
import izt.spotifyserver.repositories.UserNeo4jRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@Service
public class Neo4JUserService {

    @Autowired
    private UserNeo4jRepository userNeo4jRepo;

    @Autowired
    private InstrumentNeo4JRepository instrumentNeo4jRepo;

    public void saveNewUser(String requestBody){
        
        JsonObject jsonObject = Utils.stringToJson(requestBody);
        String username = jsonObject.getString("username");
        if(userExists(username)){
            throw new Neo4jException("User already exists");
        }
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        Neo4jUser neo4Juser = new Neo4jUser();
        neo4Juser.setUsername(username);
        neo4Juser.setFirstName(firstName);
        neo4Juser.setLastName(lastName);
        userNeo4jRepo.save(neo4Juser);
        
    }

    public JsonObject findUserInstruments(String username){
        List<Instrument> instruments = instrumentNeo4jRepo.findUserInstruments(username);
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        for(Instrument i: instruments){
            JAB.add(i.getName());
            
        }
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("instruments",JAB.build());
        return JOB.build();
    }

    public void createNewInstrument(String name){
        Instrument instrument = new Instrument();
        instrument.setName(name);
        instrumentNeo4jRepo.save(instrument);

    }

    public void addInstrumentToUser(String username, String instrument){
        if(!instrumentExists(instrument)){
            createNewInstrument(instrument);
        }

        if(!userHasAddedInstrument(username, instrument)){
            instrumentNeo4jRepo.addInstrumentToUser(username, instrument);
        }
    }

    public void deleteInstrumentRelations(String username){
        userNeo4jRepo.deleteUserInstrumentRelations(username);
    }

    public boolean userExists(String username){
        return userNeo4jRepo.findUserByUsername(username).iterator().hasNext();
    }

    public boolean userHasAddedInstrument(String username, String instrument){
        return instrumentNeo4jRepo.findUserInstrumentRelations(username, instrument).size()>0;
    }

    public boolean instrumentExists(String instrument){
        return instrumentNeo4jRepo.findInstrumentByName(instrument).size()>0;
    }

    public boolean friendRequestPending(String username, String friend){
        List<Object> result = userNeo4jRepo.userFriendRequestPending(username, friend);
        return (result.size()>0);
    }

    public boolean isFriends(String username, String friend){
        List<Object> result = userNeo4jRepo.friendStatus(username, friend);
        return (result.size()>0);
    }

    // reverse the order
    public boolean addedMe(String username, String friend){
        List<Object> result = userNeo4jRepo.userFriendRequestPending(username, friend);
        return (result.size()>0);
    }

    public String checkFriendStatus(String username, String friend){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("pending", friendRequestPending(username, friend))
            .add("friends",isFriends(username, friend))
            .add("addedme",addedMe(friend, username));
        return JOB.build().toString();
    }

    public void addFriendRequest(String username, String friend){
        userNeo4jRepo.addFriendRequest(username, friend);
    }

    public void deleteFriendRequest(String username, String friend){
        userNeo4jRepo.deleteFriendRequest(username, friend);
    }

    public void acceptFriendRequest(String username, String friend){
        userNeo4jRepo.deleteFriendRequest(username, friend);
        userNeo4jRepo.acceptFriendRequest(username, friend);
        userNeo4jRepo.acceptFriendRequest(friend, username);
    }

    public String getFriendRequests(String username){
        List<Neo4jUser> users = userNeo4jRepo.getFriendRequests(username);
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        if(users.size()>0){
        for(Neo4jUser u:users){
                JsonObjectBuilder JOB = Json.createObjectBuilder();
                JOB.add("username",u.getUsername())
                .add("firstName",u.getFirstName())
                .add("lastName",u.getLastName());
                JAB.add(JOB.build());
            }
        }
        return JAB.build().toString();
    }

    public void deleteFriend (String username, String friend){
        userNeo4jRepo.deleteFriend(username,friend);
        userNeo4jRepo.deleteFriend(friend, username);
    }
    
}
