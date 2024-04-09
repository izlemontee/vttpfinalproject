package izt.spotifyserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import izt.spotifyserver.services.MessagingService;

@RestController
@RequestMapping(path = "/message")
@CrossOrigin
public class MessagingController {

    @Autowired
    private MessagingService messagingService;

    @GetMapping(path = "/chats")
    public ResponseEntity<String> getChats(@RequestParam String username, int offset){
        String body = messagingService.getChats(username, offset);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response;
    }

    @GetMapping(path = "/startchat")
    public ResponseEntity<String> openChat(@RequestParam String user1, @RequestParam String user2){
        String body = messagingService.openChat(user1, user2);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response; 
    }

    @GetMapping(path = "/messages")
    public ResponseEntity<String> getMessages(@RequestParam String id, int offset){
        String body = messagingService.getMessages(id, offset);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response; 
    }

    @PostMapping(path = "/send")
    public ResponseEntity<String> sendMessage(@RequestBody String requestBody){
        String body = messagingService.sendMessage(requestBody);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response; 
    }

    @GetMapping(path = "/read")
    public ResponseEntity<String> readChat(@RequestParam String username, @RequestParam String id){
        messagingService.readChat(username, id);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response; 
    }

    @GetMapping(path = "/unread")
    public ResponseEntity<String> unreadChat(@RequestParam String username, @RequestParam String id){
        messagingService.unreadChat(username, id);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response; 
    }

    @GetMapping(path = "/chatinfo")
    public ResponseEntity<String> getChatInfo(@RequestParam String username, @RequestParam String id){
        String body = messagingService.getChatInfo(username, id);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body(body);
        return response; 
    }
}
