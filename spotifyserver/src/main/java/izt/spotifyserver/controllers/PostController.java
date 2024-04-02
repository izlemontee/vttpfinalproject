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

import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.services.PostService;
import jakarta.json.JsonArray;

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

    @GetMapping(path = "/posts")
    ResponseEntity<String>getPostsByUser(@RequestParam String username, @RequestParam int skip){
        String body = postService.getPostsByUser(username, skip);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }

    @PostMapping(path ="/comment")
    ResponseEntity<String>AddComment(@RequestBody String requestBody){
        String body = "{}";
        try{
            body = postService.addNewComment(requestBody);
            ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
            .body(body);
            return response;
        }catch(SQLFailedException ex){
            ResponseEntity<String> response = ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
            .body(ex.getMessage());
            return response;

        }
        

    }

    @GetMapping(path="/get")
    ResponseEntity<String> getPostById(@RequestParam String id){
        String body = postService.getPostById(id);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }

    @GetMapping(path = "/comments")
    public ResponseEntity<String> getComments(@RequestParam String id, int skip){
        JsonArray commentsArray = postService.getCommentsOfPost(id, 5, skip);
        String body = commentsArray.toString();
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }
    
}
