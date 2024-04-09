package izt.spotifyserver.repositories;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Chat;

@Repository
public interface MessagingNeo4JRepo extends  Neo4jRepository<Chat, String>{

    @Query(MessagingNeo4JQueries.READ_CHAT)
    public void readChat(String username, String id);

    @Query(MessagingNeo4JQueries.UNREAD_CHAT)
    public void unreadChat(String username, String id);

    @Query(MessagingNeo4JQueries.DELETE_READ_STATUS)
    public void deleteReadStatus(String username, String id);

    @Query(MessagingNeo4JQueries.DELETE_UNREAD_STATUS)
    public void deleteUnreadStatus(String username, String id);

    @Query(MessagingNeo4JQueries.CHECK_READ_STATUS)
    public List<Object> checkReadStatus(String username, String id);


    @Query(MessagingNeo4JQueries.GET_NUMBER_OF_UNREAD_CHATS)
    public Integer getNumberOfUnreadChats(String username);
    
}
