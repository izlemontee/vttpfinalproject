package izt.spotifyserver.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Post;

@Repository
public class PostRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long addNewPost(Post post){
        long count = jdbcTemplate.update(SqlPostQueries.SQL_ADD_NEW_POST
                        ,post.getId()
                        ,post.getUsername()
                        ,post.getTimestamp()
                        ,true,
                        post.getImage_url());
        return count;
    }

    public long addNewPostNoPicture(Post post){
        long count = jdbcTemplate.update(SqlPostQueries.SQL_ADD_NEW_POST_NO_PICTURE
        ,post.getId()
        ,post.getUsername(),
        post.getContent()
        ,post.getTimestamp()
        );
        return count;
    }
    
}
