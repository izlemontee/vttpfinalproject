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
import izt.spotifyserver.repositories.UserSQLRepository;
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

    @Autowired
    private UserSQLRepository userSqlRepo;

    private final String PLACEHOLDER_IMAGE="https://t3.ftcdn.net/jpg/05/16/27/58/360_F_516275801_f3Fsp17x6HQK0xQgDQEELoTuERO4SsWV.jpg";


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
        Post post = new Post(username, content, timestamp, has_picture, image_url);
        String profile_picture = getProfilePicture(username);
        post.setProfile_picture(profile_picture);

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

    public List<Post> processPostRowset(SqlRowSet rowset){
        List<Post> posts = new ArrayList<>();
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
            String profile_picture = getProfilePicture(usernamePost);
            Post post = new Post(id, usernamePost, content, timestamp, hasPicture, image_url);
            post.setProfile_picture(profile_picture);
            posts.add(post);
        }
        return posts;
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
                String profile_picture = getProfilePicture(usernamePost);
                Post post = new Post(id, usernamePost, content, timestamp, hasPicture, image_url);
                post.setProfile_picture(profile_picture);
                posts.add(post);
            }
        }
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        // get the post, then the comments tied to the post id from sql
        for(Post p: posts){
            SqlRowSet rowset = postRepo.getCommentsOfPost(p.getId(), 3, 0);
            JsonArray comments = processComments(rowset);
            JsonObjectBuilder builder = p.toJsonObjectBuilder();
            builder.add("comments", comments);
            int numberOfComments = getNumberOfComments(p.getId());
            builder.add("number_of_comments", numberOfComments);
            JAB.add(builder.build());
        }
        return JAB.build().toString();

    }

    public String getPostsByUser(String username, int offset){
        SqlRowSet rowset = postRepo.getPostsOfUser(username, offset);
        List<Post> posts = processPostRowset(rowset);
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        // get the post, then the comments tied to the post id from sql
        for(Post p: posts){
            JsonArray comments = getCommentsOfPost(p.getId(),3,0);
            JsonObjectBuilder builder = p.toJsonObjectBuilder();
            builder.add("comments", comments);
            int numberOfComments = getNumberOfComments(p.getId());
            builder.add("number_of_comments", numberOfComments);
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
            String profile_picture = getImage(username);
            Comment comment = new Comment(id, username, content, timestamp, post_id);
            comment.setProfile_picture(profile_picture);
            JAB.add(comment.toJson());
        }
        return JAB.build();
    }

    public String getProfilePicture(String username){
        SqlRowSet rowset = userSqlRepo.getImage(username);
        String image = PLACEHOLDER_IMAGE;
        if(rowset.next()){
            if(rowset.getString("image")!=null){
                image = rowset.getString("image");
            }
        }

        return image;
    }

    public JsonArray getCommentsOfPost(String post_id, int limit, int offset){
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        SqlRowSet rowset = postRepo.getCommentsOfPost(post_id, limit, offset);
        while(rowset.next()){
            String username = rowset.getString("username");
            String content = rowset.getString("content");
            long timestamp = rowset.getLong("timestamp");
            String image = getProfilePicture(username);
            Comment comment = new Comment(username, content, timestamp, post_id, image);
            JAB.add(comment.toJson());
        }
        return JAB.build();

    }

    @Transactional("transactionManager")
    public String addNewComment(String requestBody){
        JsonObject commentJson = Utils.stringToJson(requestBody);
        String username = commentJson.getString("username");
        String content = commentJson.getString("content");
        String post_id = commentJson.getString("post_id");
        long timestamp = new Date().getTime();
        Comment comment = new Comment(username, content, timestamp, post_id);
        long count = postRepo.addComment(comment);
        if(count<0){
            throw new SQLFailedException();
        }
        else{
            SqlRowSet imagerowset = userSqlRepo.getImage(username);
            String image = PLACEHOLDER_IMAGE;
            if(imagerowset.next()){
                if(imagerowset.getString("image") != null){
                    image = imagerowset.getString("image");
                }
            }
            comment.setProfile_picture(image);
            JsonObject commentJsonReturn = comment.toJson();
            return commentJsonReturn.toString();
        }

    }

    public String getPostById(String id){
        SqlRowSet rowset = postRepo.getPostById(id);
        if(rowset.next()){
            String username = rowset.getString("username");
            String content = rowset.getString("content");
            long timestamp = rowset.getLong("timestamp");
            String profile_picture = getImage(username);
            Post post = new Post(id, username, content, timestamp);
            post.setProfile_picture(profile_picture);
            int numberOfComments = getNumberOfComments(id);
            JsonObject postJson = post.toJsonWithNumberOfComments(numberOfComments);
            return postJson.toString();
        }
        else{
            return "{}";
        }
    }

    public String getImage(String username){
        SqlRowSet rowset = userSqlRepo.getImage(username);
        String image = PLACEHOLDER_IMAGE;
        if(rowset.next()){
            if(rowset.getString("image")!=null){
                image = rowset.getString("image");
            }
        }
        return image;
    }


    public int getNumberOfComments(String post_id){
        SqlRowSet rowSet = postRepo.getNumberOfComments(post_id);
        rowSet.next();
        int count = rowSet.getInt("count(*)");
        return count;
    }
}
