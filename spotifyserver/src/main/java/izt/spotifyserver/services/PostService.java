package izt.spotifyserver.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.models.Post;
import izt.spotifyserver.repositories.PostNeo4JRepository;
import izt.spotifyserver.repositories.PostRepository;
import jakarta.json.JsonObject;

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
        long count = postRepo.addNewPostNoPicture(post);
        if(count<1){
            throw new SQLFailedException();
        }
    }
    
    public void getPostsForFeed(String username, int skip){
        List<String> postIdList = postNeo4jRepo.getPostIds(username, skip);
        // get the post, then the comments tied to the post id from sql
        System.out.println(postIdList.toString());
    }
}
