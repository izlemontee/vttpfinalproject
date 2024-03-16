package izt.spotifyserver.repositories;

public class Neo4JQueries {

    public static final String NEO4J_CREATE_USER="""
            
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
    public static final String NEO4J_ACCEPT_FRIEND_REQUEST="""
        MATCH(user: User {username:$username}),(friend: User {username:$friend})
        CREATE (user)-[:FRIENDS_WITH]->(friend)
        
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
    public static final String NEO4J_FIND_FRIENDS="""
        MATCH(user: User {username:$username})
        -[:FRIENDS_WITH]->(friend: User)
        RETURN friend
            """;
}
