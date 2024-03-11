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






} 