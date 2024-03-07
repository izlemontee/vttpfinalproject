package izt.spotifyserver.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Artist;
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
        int count = jdbcTemplate.update(Queries.SQL_ADD_ACCESS_KEY, 
        user.getAccessKey(), 
        user.getRefreshToken(),
        user.getUsername());
        return count;
    }

    public SqlRowSet getUserAccessKeyAndRefresh(String username){
        SqlRowSet result = jdbcTemplate.queryForRowSet(Queries.SQL_GET_USER_ACCESSKEY_AND_REFRESH, username);
        return result;
    }

    public void updateUserAccessKey(User user){
        System.out.println("access key:"+user.getAccessKey());
        System.out.println(user.getUsername());
        jdbcTemplate.update(Queries.SQL_UPDATE_ACCESS_KEY,
                            user.getAccessKey()
                            ,user.getUsername());
                            System.out.println("done");
    }

    public int addUserEmailAccessKeyRefreshToken(User user){
        int count = jdbcTemplate.update(Queries.SQL_ADD_EMAIL_ACCESS_KEY_REFRESH,
                            user.getEmail(),
                            user.getAccessKey(),
                            user.getRefreshToken(),
                            user.getUsername());
        return count;
    }

    public SqlRowSet getUserNameAndBio(String username){
        System.out.println("username: "+username);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(Queries.SQL_GET_NAME_AND_BIO, username);
        return rowSet;

    }

    public void updateProfileNameAndBio(User user){
        jdbcTemplate.update(Queries.SQL_UPDATE_NAME_AND_BIO,
                            user.getFirstName(),
                            user.getLastName(),
                            user.getBio(),
                            user.getUsername());
                            

    }

    public int addArtist(Artist artist){
        return jdbcTemplate.update(Queries.SQL_ADD_ARTIST, 
                            artist.getName(),
                            artist.getImage(),
                            artist.getUrl(),
                            artist.getUsername());
    }

    public void deleteArtists(String username){
        jdbcTemplate.update(Queries.SQL_DELETE_ARTISTS, username);
    }


    
}