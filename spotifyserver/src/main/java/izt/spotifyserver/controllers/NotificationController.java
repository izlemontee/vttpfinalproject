package izt.spotifyserver.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import izt.spotifyserver.services.NotificationService;

@RestController
@RequestMapping(path = "/notification")
@CrossOrigin
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewNotification(@RequestBody String requestBody)throws IOException{
        notificationService.addNewNotification(requestBody);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response;

    }
    
    @GetMapping(path ="/glance")
    public ResponseEntity<String> getNotificationsGlance(@RequestParam String username){
        String body = notificationService.getNotificationsGlance(username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }

    @GetMapping(path = "/get")
    public ResponseEntity<String> getNotifications(@RequestParam String username){
        String body = notificationService.getNotifications(username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }

    @GetMapping(path = "/read")
    public ResponseEntity<String> readNotification(@RequestParam(name="id") int id)throws IOException{
        // int id = Integer.parseInt(idString);
        notificationService.readNotification(id);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(("{}"));
        return response;
    }

    @GetMapping(path = "/unreadnumber")
    public ResponseEntity<String> getNumberOfUnreadNotifs(@RequestParam String username){
        String body = notificationService.getNumberOfUnreadNotifsBody(username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }
}
