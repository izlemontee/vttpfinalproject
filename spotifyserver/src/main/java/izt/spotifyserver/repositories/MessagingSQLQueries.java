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


        SELECT c.id, c.user1, c.user2, c.last_updated
        FROM chats as c
        JOIN messages as m ON c.id = m.chat_id
        WHERE c.user1 = ? or c.user2 = ?
        GROUP BY c.id
        HAVING COUNT(m.chat_id) > 0
        ORDER BY c.last_updated DESC
        LIMIT 5
        OFFSET ?;
            """;

            // SELECT * from chats
            // WHERE user1 = ? OR user2 = ?
            // ORDER BY last_updated DESC
            // LIMIT 5
            // OFFSET ?; 

    public static final String CHAT_EXISTS="""
        SELECT id from chats
        WHERE (user1 = ? AND user2= ?) 
        OR (user1= ? AND user2= ?);
            """;

    public static final String GET_CHAT_INFO="""
        SELECT * from chats
        WHERE id = ?
            """;

    

}
