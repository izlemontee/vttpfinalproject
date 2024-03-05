package izt.spotifyserver.repositories;

public class Queries {

    public static final String SQL_CREATE_NEW_USER="""
            INSERT INTO users (username, password, email, firstname, lastname)
            VALUES(?,?,?,?,?)
            """;

    public static final String SQL_FIND_USERNAME_BY_USERNAME="""
            SELECT username from users
            WHERE username = ?
            """;

    public static final String SQL_CHECK_USERNAME_AND_PASSWORD="""
        SELECT username FROM users
        WHERE username = ?
        AND password = ?;
            """;

    public static final String SQL_ADD_USER_SESSION="""
        INSERT INTO usersessions (sessionid,username,password)
        VALUES (?, ?,?)
            """;
    public static final String SQL_REMOVE_USER_SESSION="""
            DELETE FROM usersessions
            WHERE sessionid = ?
            """;
    public static final String SQL_ADD_ACCESS_KEY="""
            UPDATE users
            SET access_key = ?
            WHERE username = ?
            """;
}
