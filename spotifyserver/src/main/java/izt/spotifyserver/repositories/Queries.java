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
            SET access_key = ?, refresh_token = ?
            WHERE username = ?
            """;

        public static final String SQL_GET_USER_ACCESSKEY_AND_REFRESH="""
                        SELECT username,access_key,refresh_token
                        FROM users
                        WHERE username = ?
                        """;

        public static final String SQL_UPDATE_ACCESS_KEY="""
                        UPDATE users 
                        SET access_key = ?
                        WHERE username = ?
                        """;

        public static final String SQL_ADD_EMAIL_ACCESS_KEY_REFRESH="""
                        UPDATE users 
                        SET spotify_email = ?, spotify_linked = true, access_key = ?, refresh_token = ?
                        WHERE username = ?
                        """;

        public static final String SQL_GET_NAME_AND_BIO="""
                        SELECT username,firstname,lastname, bio, spotify_linked
                        FROM users
                        WHERE username = ?
                        """;
        public static final String SQL_UPDATE_NAME_AND_BIO="""
                        UPDATE users
                        SET firstname =?, lastname = ?, bio = ?
                        WHERE username = ?
                        """;
        public static final String SQL_ADD_ARTIST="""
                        INSERT INTO artists (name, image, url, username)
                        VALUES(?, ?, ?, ?)

                        """;
        public static final String SQL_DELETE_ARTISTS="""
                        DELETE FROM artists
                        WHERE username = ?
                        """;
        public static final String SQL_GET_USER_INFO="""
                        SELECT * FROM users
                        WHERE username = ?
                        """;
        public static final String SQL_GET_ARTISTS="""
                        SELECT * FROM artists
                        WHERE username = ?
                        """;
        public static final String SQL_ADD_IMAGE_URL_USER="""
                        UPDATE users
                        SET image = ?
                        WHERE username = ?
                        """;
        public static final String SQL_FIND_SESSION_AND_USER="""
                        SELECT sessionid, username
                        FROM usersessions
                        WHERE sessionid = ? AND username = ?
                        """;
        public static final String SQL_FIND_GENRES_BY_USER="""
                        SELECT name
                        FROM genres
                        WHERE username = ?
                        """;

        public static final String SQL_DELETE_GENRES="""
                        DELETE from genres
                        WHERE username=?
                        """;
        public static final String SQL_ADD_GENRE_TO_USER="""
                        INSERT INTO genres (name, username)
                        VALUES(?,?)
                        """;
        public static final String SQL_ADD_NEW_NOTIFICATION="""
                INSERT INTO notifications (username, text, url, type, timestamp)
                VALUES(?, ?, ?, ?,?)
                        """;
        public static final String SQL_READ_NOTIFICATION="""
                UPDATE notifications
                SET notification_read = true
                WHERE id = ?
                """;
        public static final String SQL_GET_NOTIFICATIONS_GLANCE="""
                SELECT * FROM notifications
                WHERE username = ?   
                LIMIT 5     
                """;

        public static final String SQL_GET_SINGLE_NOTIFICATION="""
                        SELECT * from notifications
                        WHERE id = ?
                        """;

        public static final String SQL_GET_NOTIFICATIONS="""
                SELECT * FROM notifications
                WHERE username = ?
                ORDER BY timestamp DESC;     
                """;
        public static final String SQL_GET_USERNAME_NAME_IMAGE="""
                SELECT username, firstname, lastname, image
                FROM users
                WHERE username LIKE ?
                        """;
        public static final String SQL_GET_NUMBER_UNREAD_NOTIFS="""
                SELECT COUNT(notification_read)
                FROM notifications
                WHERE username = ?
                AND notification_read = false;
                        """;
        public static final String SQL_GET_USER_IMAGE="""
                SELECT image
                FROM users
                WHERE username = ?
                        """;
}
