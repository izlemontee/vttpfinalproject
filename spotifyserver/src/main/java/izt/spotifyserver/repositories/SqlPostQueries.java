package izt.spotifyserver.repositories;

public class SqlPostQueries {
    public static final String SQL_ADD_NEW_POST="""
            INSERT INTO posts (id,username,content, timestamp, has_picture, image_url, profile_picture)
            VALUES(?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String SQL_ADD_NEW_POST_NO_PICTURE="""
        INSERT INTO posts (id,username,content, timestamp)
        VALUES(?, ?, ?, ?)
            """;
    public static final String SQL_GET_POSTS_BY_USER="""
            SELECT * FROM posts
            WHERE username = ?
            ORDER BY timestamp DESC
            LIMIT 5
            OFFSET ?;   
            """;
    public static final String SQL_GET_COMMENTS_FOR_POST="""
            SELECT * FROM comments
            WHERE post_id=?
            ORDER BY timestamp DESC
            LIMIT ?
            OFFSET ?
            """;
    public static final String SQL_ADD_COMMENT="""
            INSERT INTO comments(username, content, timestamp, post_id)
            VALUES(?, ?, ?, ?)
            """;
    
    



}
