package izt.spotifyserver.repositories;

public class MessagingSQLQueries {

    public static final String START_NEW_CHAT="""
        INSERT INTO chats (id, user1, user2, last_updated)
        VALUES (?, ?, ?, ?)
            """;

    public static final String SEND_MESSAGE="""
        INSERT INTO messages (sender, recipient, content, chat_id, timestamp)
        VALUES(? ,? ,?, ?, ?)
            """;
    public static final String UPDATE_CHAT_TIMESTAMP="""
        UPDATE chats
        SET last_updated = ?
        WHERE id = ?
            """;

    public static final String GET_MESSAGES="""
        SELECT * from messages
        WHERE chat_id = ?
        ORDER BY timestamp DESC
        LIMIT 5
        OFFSET ?; 
            """;
    public static final String GET_CHATS="""
        SELECT * from chats
        WHERE user1 = ? OR user2 = ?
        ORDER BY last_updated DESC
        LIMIT 5
        OFFSET ?; 
            """;

    public static final String CHAT_EXISTS="""
        SELECT id from chats
        WHERE (user1 = ? AND user2= ?) 
        OR (user1= ? AND user2= ?);
            """;

}
