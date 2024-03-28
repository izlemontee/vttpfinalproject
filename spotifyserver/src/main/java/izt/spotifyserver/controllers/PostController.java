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

import izt.spotifyserver.services.PostService;

@RestController
@RequestMapping(path = "/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(path = "/new")
    ResponseEntity<String> addNewPost(@RequestBody String requestBody){
        System.out.println(requestBody);
        postService.addNewPost(requestBody);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response;
    }

    @GetMapping(path = "/feed")
    ResponseEntity<String>getPostsForFeed(@RequestParam String username, @RequestParam int skip){
        String body = postService.getPostsForFeed(username, skip);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }
    
}
