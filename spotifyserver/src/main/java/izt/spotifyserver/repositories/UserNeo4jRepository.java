package izt.spotifyserver.repositories;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Neo4jUser;
import izt.spotifyserver.models.User;
import reactor.core.publisher.Flux;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<Neo4jUser,String> {

    @Query("MATCH(user:User) WHERE(user.username=$username) RETURN user")
    Iterable<Neo4jUser> findUserByUsername(String username);

    @Query(Neo4JQueries.NEO4J_DELETE_INSTRUMENT_RELATIONS)
    void deleteUserInstrumentRelations(String username);

    @Query(Neo4JQueries.NEO4J_CHECK_FRIEND_REQUEST_PENDING)
    List<Object> userFriendRequestPending(String username, String friend);

    @Query(Neo4JQueries.NEO4J_CHECK_FRIEND_STATUS)
    List<Object> friendStatus(String username, String friend);

    @Query(Neo4JQueries.NEO4J_SEND_FRIEND_REQUEST)
    void addFriendRequest(String username, String friend);

    @Query(Neo4JQueries.NEO4J_DELETE_FRIEND_REQUEST)
    void deleteFriendRequest(String username, String friend);

    @Query(Neo4JQueries.NEO4J_ACCEPT_FRIEND_REQUEST)
    void acceptFriendRequest(String username, String friend);

    @Query(Neo4JQueries.NEO4J_GET_FRIEND_REQUESTS)
    List<Neo4jUser> getFriendRequests(String username);

    @Query(Neo4JQueries.NEO4J_DELETE_FRIEND)
    void deleteFriend(String username, String friend);



} 