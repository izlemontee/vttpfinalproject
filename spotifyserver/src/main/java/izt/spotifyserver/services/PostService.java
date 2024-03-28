package izt.spotifyserver.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.models.Comment;
import izt.spotifyserver.models.Post;
import izt.spotifyserver.repositories.PostNeo4JRepository;
import izt.spotifyserver.repositories.PostRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private PostNeo4JRepository postNeo4jRepo;

    public void addNewPost(String requestBody){
        JsonObject jsonBody = Utils.stringToJson(requestBody);
        String username = jsonBody.getString("username");
        String content = jsonBody.getString("content");
        boolean has_picture = jsonBody.getBoolean("has_picture");
        String image_url = "";
        if(has_picture){
            image_url = jsonBody.getString("image_url");
        }
        long timestamp = new Date().getTime();
        Post post = new Post(username, content, timestamp);

        addNewPostToSql(post);
        addNewPostToNeo4j(post);

    }

    public void addNewPostToNeo4j(Post post){
        postNeo4jRepo.save(post);
        postNeo4jRepo.linkPostToUser(post.getUsername(), post.getId());
    }

    @Transactional
    public void addNewPostToSql(Post post){
        long count = postRepo.addNewPost(post);
        if(count<1){
            throw new SQLFailedException();
        }
    }
    
    public String getPostsForFeed(String username, int skip){
        List<String> ids = postNeo4jRepo.getPostIds(username, skip);
        List<Post> posts = new ArrayList<>();
        for(String s :ids){
            SqlRowSet rowset = postRepo.getPostById(s);
            while(rowset.next()){
                String id = rowset.getString("id");
                String usernamePost = rowset.getString("username");
                String content = rowset.getString("content");
                long timestamp = rowset.getLong("timestamp");
                boolean hasPicture = rowset.getBoolean("has_picture");
                String image_url = "";
                if(hasPicture){
                    image_url = rowset.getString("image_url");
                }
                Post post = new Post(id, usernamePost, content, timestamp, hasPicture, image_url);
                posts.add(post);
            }
        }
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        // get the post, then the comments tied to the post id from sql
        for(Post p: posts){
            SqlRowSet rowset = postRepo.getComments(p.getId());
            JsonArray comments = processComments(rowset);
            JsonObjectBuilder builder = p.toJsonObjectBuilder();
            builder.add("comments", comments);
            JAB.add(builder.build());
        }
        return JAB.build().toString();

    }

    public JsonArray processComments(SqlRowSet rowset){
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        while(rowset.next()){
            int id = rowset.getInt("id");
            String username = rowset.getString("username");
            String content = rowset.getString("content");
            long timestamp = rowset.getLong("timestamp");
            String post_id = rowset.getString("post_id");
            Comment comment = new Comment(id, username, content, timestamp, post_id);
            JAB.add(comment.toJson());
        }
        return JAB.build();
    }
}
