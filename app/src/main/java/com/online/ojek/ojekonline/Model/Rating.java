package com.online.ojek.ojekonline.Model;

/**
 * Created by adib on 25/05/18.
 */

public class Rating {
    private String ratings;
    private String comments;

    public Rating() {
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Rating(String ratings, String comments) {
        this.ratings = ratings;
        this.comments = comments;
    }
}
