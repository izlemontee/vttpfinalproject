package izt.spotifyserver.repositories;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Instrument;
import izt.spotifyserver.models.Post;

@Repository
public interface PostNeo4JRepository extends Neo4jRepository<Post,String>{


    @Query(Neo4JQueries.NEO4J_GET_POST_BY_ID)
    public List<Post> getPost();

    @Query(Neo4JQueries.NEO4J_CREATE_POST_RELATION)
    public void linkPostToUser(String username, String id);

    @Query(Neo4JQueries.NEO4J_GET_POSTS_FOR_FEED)
    public List<String> getPostIds(String username,int skip);

    
    
}
