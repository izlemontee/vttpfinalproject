package izt.spotifyserver.repositories;

public class MessagingNeo4JQueries {

    public static final String READ_CHAT="""
        MATCH (user: User {username: $username}), (chat: Chat {id: $id})
        CREATE (user)-[:READ]->(chat)
            """;
    public static final String UNREAD_CHAT="""
        MATCH (user: User {username: $username}), (chat: Chat {id: $id})
        CREATE (user)-[:UNREAD]->(chat)   
            """;

    public static final String DELETE_READ_STATUS="""
        MATCH (user: User {username: $username}) -[status:READ]-> (chat: Chat {id: $id})
        DELETE status
            """;

    public static final String DELETE_UNREAD_STATUS="""
        MATCH (user: User {username: $username}) -[status:UNREAD]-> (chat: Chat {id: $id})
        DELETE status
            """;
        
    public static final String CHECK_READ_STATUS="""
        MATCH (user: User {username: $username}) -[status:READ]-> (chat: Chat {id: $id})
        RETURN status
            """;

    public static final String CHECK_UNREAD_STATUS="""
                MATCH (user: User {username: $username}) -[status:UNREAD]-> (chat: Chat {id: $id})
                RETURN status
                    """;
    
}
