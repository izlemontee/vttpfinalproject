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

    public JsonObject findUserInstruments(){
        List<Instrument> instruments = instrumentNeo4jRepo.findUserInstruments("izlemontee");
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        for(Instrument i: instruments){
            JAB.add(i.getName());
            
        }
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("instruments",JAB.build());
        return JOB.build();
    }

    public boolean userExists(String username){
        return userNeo4jRepo.findUserByUsername(username).iterator().hasNext();
    }

    public boolean userHasAddedInstrument(String username, String instrument){
        return instrumentNeo4jRepo.findUserInstrumentRelations(username, instrument).size()>0;
    }
    
}
