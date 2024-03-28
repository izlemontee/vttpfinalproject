package izt.spotifyserver.repositories;

public class Neo4JQueries {

    public static final String NEO4J_CREATE_USER="""
        CREATE (user:User {username:$username,firstName:$firstName,lastName:$lastName});
            """;
    

    public static final String NEO4J_CREATE_INSTRUMENT="""
        CREATE (instrument:Instrument  {name:$instrument})
            """;
    public static final String NEO4J_CREATE_BAND="""
        CREATE (band:Band  {name:$name})
            """;

    public static final String NEO4J_ADD_INSTRUMENT_TO_USER="""
        MATCH(user:User {username:$username}),(instrument:Instrument {name:$instrument})
        CREATE (user)-[:PLAYS]->(instrument)
            """;

    public static final String NEO4J_FIND_INSTRUMENTS_BY_USER="""
        MATCH (user:User)-[:PLAYS]->(instrument:Instrument) 
        WHERE user.username=$username
        RETURN instrument;
            """;

    public static final String NEO4J_INSTRUMENT_ADDED_BY_USER="""   
        MATCH(user:User {username:$username})
        -[plays:PLAYS]->(instrument:Instrument {name:$instrument})
        RETURN plays
            """;
    public static final String NEO4J_FIND_INSTRUMENT_BY_NAME="""
            MATCH(instrument:Instrument {name:$name})
            RETURN instrument
            """;
    public static final String NEO4J_DELETE_INSTRUMENT_RELATIONS="""
            MATCH(user: User {username:$username})
            -[plays:PLAYS]-> (instrument:Instrument)
            DELETE plays
            """;
    public static final String NEO4J_SEND_FRIEND_REQUEST="""
            MATCH(user: User {username:$username}),(friend: User {username:$friend})
            CREATE (user)-[:ADDED]->(friend)
            
            """;

    public static final String NEO4J_CHECK_FRIEND_REQUEST_PENDING="""
            MATCH(user: User{username:$username})
            -[request:ADDED]->(friend:User{username:$friend})
            return request

            """;
    public static final String NEO4J_ACCEPT_FRIEND_REQUEST="""
        MATCH(user: User {username:$username}),(friend: User {username:$friend})
        CREATE (user)-[:FRIENDS_WITH]->(friend)
        
            """;
    public static final String NEO4J_CHECK_FRIEND_STATUS="""
            MATCH(user: User{username:$username})
            -[friendswith:FRIENDS_WITH]->(friend: User {username:$friend})
            RETURN friendswith
            """;
    public static final String NEO4J_DELETE_FRIEND_REQUEST="""
        MATCH(user: User {username:$username})
        -[request:ADDED]->(friend: User {username:$friend})
        DELETE request
        
            """;
    public static final String NEO4J_DELETE_FRIEND="""
            
    MATCH(user: User {username:$username})
    -[request:FRIENDS_WITH]->(friend: User {username:$friend})
    DELETE request
    
            """;

    public static final String NEO4J_GET_FRIEND_REQUESTS="""
            MATCH(user: User{username:$username})
            <-[:ADDED]-(friend: User)
            RETURN friend
            """;
    public static final String NEO4J_FIND_FRIENDS="""
        MATCH(user: User {username:$username})
        -[:FRIENDS_WITH]->(friend: User)
        RETURN friend
            """;
        public static final String NEO4J_FIND_NUMBER_OF_FRIENDS="""
                MATCH(user: User {username:$username})
                -[:FRIENDS_WITH]->(friend: User)
                RETURN count(friend)
                        """;

        public static final String NEO4J_CREATE_POST_RELATION="""
                        
        MATCH (user:User {username:$username}),(post:Post {id:$id})
        CREATE (user)-[:POSTED]->(post)
                        """;

        public static final String NEO4J_GET_POST_BY_ID="""
        MATCH(post:Post {id:$id})
        RETURN post
                        """;

        public static final String NEO4J_GET_POSTS_FOR_FEED="""
        CALL{
                MATCH(user:User)-[:POSTED]->(post:Post)WHERE(user.username = $username)
                RETURN post AS post
                
                UNION
                
                MATCH(user)-[:FRIENDS_WITH]->(friend:User)-[:POSTED]->(friendPost:Post)
                RETURN friendPost AS post
        }
                    
        RETURN post.id
        ORDER BY post.timestamp DESC
        SKIP $skip
        LIMIT 5
        """;
}
