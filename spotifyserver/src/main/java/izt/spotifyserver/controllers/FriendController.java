package izt.spotifyserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import izt.spotifyserver.services.Neo4JUserService;
import izt.spotifyserver.services.UserService;

@RestController
@RequestMapping(path = "/friend")
@CrossOrigin
public class FriendController {
    

    @Autowired
    private Neo4JUserService neo4jUserService;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/status")
    public ResponseEntity<String> checkFriendStatus(@RequestParam String username, @RequestParam String friend){
        String body = neo4jUserService.checkFriendStatus(username, friend);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response;
    }

    @GetMapping(path = "/add")
    public ResponseEntity<String> addFriendRequest(@RequestParam String username, @RequestParam String friend){
        neo4jUserService.addFriendRequest(username, friend);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response;
    }

    @DeleteMapping(path = "/deleterequest")
    public ResponseEntity<String> deleteFriendRequest(@RequestParam String username, @RequestParam String friend){
        neo4jUserService.deleteFriendRequest(username, friend);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response;
    }

    @GetMapping(path = "/requests")
    public ResponseEntity<String> getFriendRequests(@RequestParam String username){
        String body = neo4jUserService.getFriendRequests(username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response;
    }

    @GetMapping(path = "/accept")
    public ResponseEntity<String> acceptFriendRequest(@RequestParam String username, @RequestParam String friend){
        neo4jUserService.acceptFriendRequest(username, friend);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response;
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<String> deleteFriend(@RequestParam String username, @RequestParam String friend){
        neo4jUserService.deleteFriend(username, friend);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response;
    }

    @GetMapping(path = "/number")
    public ResponseEntity<String> getNumberOfFriends(@RequestParam String username){
        String body = neo4jUserService.getNumberOfFriends(username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response;
    }

    @GetMapping(path ="/myfriends")
    public ResponseEntity<String> getFriends(@RequestParam String username, @RequestParam int skip){
        String body = userService.getFriends(username, skip);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }
}
