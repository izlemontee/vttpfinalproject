package izt.spotifyserver.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.User;


@Repository
public class UserSQLRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createNewUser(User user){
        int insertCount = jdbcTemplate.update(Queries.SQL_CREATE_NEW_USER,
                            user.getUsername()
                            ,user.getPassword()
                            ,user.getEmail()
                            ,user.getFirstName()
                            ,user.getLastName());
        return insertCount;
    }

    public boolean userExists(String username){
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(Queries.SQL_FIND_USERNAME_BY_USERNAME,username);
        return rowSet.next();

    }

    public boolean usernamePasswordMatch (User user){

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(Queries.SQL_CHECK_USERNAME_AND_PASSWORD, user.getUsername(), user.getPassword());
        return (rowSet.next());
    }

    public int addUserSession(User user){
        int count = jdbcTemplate.update(Queries.SQL_ADD_USER_SESSION,
                                user.getSessionId(),
                                user.getUsername(),
                                user.getPassword());
        return count;
    }

    public int deleterUserSession(String sessionid){
        int count  =jdbcTemplate.update(Queries.SQL_REMOVE_USER_SESSION, sessionid);
        return count;
    }

    public int addAccessKey(User user){
        int count = jdbcTemplate.update(Queries.SQL_ADD_ACCESS_KEY, user.getAccessKey(), user.getUsername());
        return count;
    }







    
}
