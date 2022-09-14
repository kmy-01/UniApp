package com.codingstuff.todolist.LoginPost;

import java.io.Serializable;
import java.util.Map;

public class Post implements Serializable {
    private String postID;
    private Map posts;

    public Post(String postID, Map posts) {
        this.postID = postID;
        this.posts = posts;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Map getPosts() {
        return posts;
    }

    public void setPosts(Map posts) {
        this.posts = posts;
    }
}
