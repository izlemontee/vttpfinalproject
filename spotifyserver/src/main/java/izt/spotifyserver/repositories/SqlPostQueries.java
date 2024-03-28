package izt.spotifyserver.repositories;

public class SqlPostQueries {
    public static final String SQL_ADD_NEW_POST="""
            INSERT INTO posts (id,username,content, timestamp, has_picture, image_url)
            VALUES(?, ?, ?, ?, ?, ?)
            """;

    public static final String SQL_ADD_NEW_POST_NO_PICTURE="""
        INSERT INTO posts (id,username,content, timestamp)
        VALUES(?, ?, ?, ?)
            """;
    



}
