package izt.spotifyserver.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Post;

@Repository
public class PostRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long addNewPost(Post post){
        long count = jdbcTemplate.update(SqlPostQueries.SQL_ADD_NEW_POST
                        ,post.getId()
                        ,post.getUsername(),
                        post.getContent()
                        ,post.getTimestamp()
                        ,post.isHas_picture(),
                        post.getImage_url(),
                        post.getProfile_picture());
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

    public SqlRowSet getPostById(String id){
        SqlRowSet rowset  = jdbcTemplate.queryForRowSet(Queries.SQL_GET_POST_BY_ID, id);
        return rowset;
    }

    public SqlRowSet getComments(String id){
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(Queries.SQL_GET_COMMENTS, id);
        return rowSet;
    }

    public SqlRowSet getPostsOfUser(String username, int offset){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(SqlPostQueries.SQL_GET_POSTS_BY_USER, username, offset);
        return rowset;
    }




    
}
