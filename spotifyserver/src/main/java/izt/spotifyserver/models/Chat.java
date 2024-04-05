package izt.spotifyserver.models;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Chat")
public class Chat {

    @Id
    private String id;

    private String user1;
    private String user2;
    private Long last_updated;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUser1() {
        return user1;
    }
    public void setUser1(String user1) {
        this.user1 = user1;
    }
    public String getUser2() {
        return user2;
    }
    public void setUser2(String user2) {
        this.user2 = user2;
    }
    public Long getLast_updated() {
        return last_updated;
    }
    public void setLast_updated(Long last_updated) {
        this.last_updated = last_updated;
    }
    
}
