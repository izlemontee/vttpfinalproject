package izt.spotifyserver.repositories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import izt.spotifyserver.models.Artist;
import izt.spotifyserver.models.Notification;
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

    public SqlRowSet getUserProfile(String username){
        return jdbcTemplate.queryForRowSet(Queries.SQL_GET_USER_INFO, username);
    }
    public SqlRowSet getArtists(String username){
        return jdbcTemplate.queryForRowSet(Queries.SQL_GET_ARTISTS, username);
    }

    public long addImageToUser(String imageUrl, String username){
        return jdbcTemplate.update(Queries.SQL_ADD_IMAGE_URL_USER, imageUrl, username);
    }

    public boolean sessionValid(String sessionid, String username){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(Queries.SQL_FIND_SESSION_AND_USER, sessionid, username);
        return rowset.next();

    }

    public SqlRowSet getGenres(String username){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(Queries.SQL_FIND_GENRES_BY_USER, username);
        return rowset;
    }

    public long deleteGenres(String username){
        long count = jdbcTemplate.update(Queries.SQL_DELETE_GENRES, username);
        return count;
    }

    public long addGenres(String genre, String username){
        long count = jdbcTemplate.update(Queries.SQL_ADD_GENRE_TO_USER, genre, username);
        return count;
    }

    public long addNewNotification(Notification notification){
        long count = jdbcTemplate.update(Queries.SQL_ADD_NEW_NOTIFICATION,
                    notification.getUsername(),
                    notification.getText(),
                    notification.getUrl(),
                    notification.getType(),
                    notification.getTimestamp().getTime());
        return count;

    }   
    public long readNotification(int id){
        System.out.println("read notif");
        long count = jdbcTemplate.update(Queries.SQL_READ_NOTIFICATION, id);
        return count;
    }

    public SqlRowSet getNotificationsGlance(String username){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(Queries.SQL_GET_NOTIFICATIONS_GLANCE, username);
        return rowset;
    }

    public SqlRowSet getNotifications(String username, int offset){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(Queries.SQL_GET_NOTIFICATIONS, username, offset);
        return rowset;
    }

    public SqlRowSet searchForUsers(String searchTerm){
        searchTerm = "%"+searchTerm+"%";
        SqlRowSet rowset= jdbcTemplate.queryForRowSet(Queries.SQL_GET_USERNAME_NAME_IMAGE,searchTerm);
        return rowset;
    }

    public SqlRowSet getNumberOfUnreadNotifications(String username){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(Queries.SQL_GET_NUMBER_UNREAD_NOTIFS,username);
        return rowset;
    }

    public SqlRowSet getImage(String username){
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(Queries.SQL_GET_USER_IMAGE, username);
        return rowSet;
    }

    public SqlRowSet getSingleNotification(int id){
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(Queries.SQL_GET_SINGLE_NOTIFICATION, id);
        return rowSet;
    }

    public SqlRowSet getUsernameNameImagebyUsername(String username){
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(Queries.SQL_GET_USERNAME_NAME_IMAGE_EXACT, username);
        return rowset;
    }

    public SqlRowSet getUsersFromList(List<String> usernames){
        String usernameList = usernames.stream()
                                    //    .map(username -> "'" + username + "'")
                                       .collect(Collectors.joining(","));
        System.out.println(usernameList);
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(Queries.SQL_GET_USERS_FROM_LIST, usernameList);
        
        return rowset;
    }

    
}
