package izt.spotifyserver.repositories;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Instrument;
import izt.spotifyserver.models.Neo4jUser;

@Repository
public interface InstrumentNeo4JRepository extends Neo4jRepository<Instrument,Long>{

    @Query(Neo4JQueries.NEO4J_FIND_INSTRUMENTS_BY_USER)
    List<Instrument> findUserInstruments(String username);

    @Query(Neo4JQueries.NEO4J_INSTRUMENT_ADDED_BY_USER)
    List<Instrument> findUserInstrumentRelations(String username, String instrument);

    @Query(Neo4JQueries.NEO4J_CREATE_INSTRUMENT)
    void createNewInstrument(String instrument);

    @Query(Neo4JQueries.NEO4J_ADD_INSTRUMENT_TO_USER)
    void addInstrumentToUser(String username, String instrument);
    
    @Query(Neo4JQueries.NEO4J_FIND_INSTRUMENT_BY_NAME)
    List<Instrument> findInstrumentByName(String name);



    
}