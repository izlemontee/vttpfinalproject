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
}
