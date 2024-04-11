package izt.spotifyserver.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Chat;
import izt.spotifyserver.models.Message;

@Repository
public class MessagingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long startNewChat(Chat chat){
        long count = jdbcTemplate.update(MessagingSQLQueries.START_NEW_CHAT,
                        chat.getId(),
                        chat.getUser1(),
                        chat.getUser2(),
                        chat.getLast_updated());
        return count;
        
    }

    public long sendMessage(Message message){
        long count = jdbcTemplate.update(MessagingSQLQueries.SEND_MESSAGE,
                    message.getSender(),
                    message.getRecipient(),
                    message.getContent(),
                    message.getChat_id(),
                    message.getTimestamp());
        return count;
    }

    public long updateChatTimestamp(long timestamp, String id){
        long count = jdbcTemplate.update(MessagingSQLQueries.UPDATE_CHAT_TIMESTAMP,
                    timestamp,
                    id);
        return count;
    }

    public SqlRowSet getMessages (String chat_id, int offset){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(MessagingSQLQueries.GET_MESSAGES,
                            chat_id,
                            offset);
        return rowset;
    }

    public SqlRowSet getChats(String username, int offset){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(MessagingSQLQueries.GET_CHATS,
                            username,
                            username,
                            offset);
        return rowset;
    }

    public boolean chatExists(String user1, String user2){
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(MessagingSQLQueries.CHAT_EXISTS,
                            user1, user2,
                            user2, user1);
        return rowSet.next();
    }

    public SqlRowSet getChatId(String user1, String user2){
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(MessagingSQLQueries.CHAT_EXISTS,
                        user1, user2,
                        user2, user1);
        return rowSet;
    }

    public SqlRowSet getChatInfo (String id){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(MessagingSQLQueries.GET_CHAT_INFO, id);
        return rowset;
    }

    public SqlRowSet getIdOfLatestChat(String username){
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(MessagingSQLQueries.GET_ID_LATEST_CHAT, username, username);
        return rowSet;
    }
    
}
