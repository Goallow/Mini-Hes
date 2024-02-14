package edu.dgut.pojo;

/**
 * @Author Goallow
 * @Date 2021/11/15 9:31
 * @Version 1.0
 */
public class Entry {
    private int userId;
    private int itemId;
    /**
     * 实际评分
     */
    private double rating;
    /**
     * 估计评分
     */
    private double ratingHat;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRatingHat() {
        return ratingHat;
    }

    public void setRatingHat(double ratingHat) {
        this.ratingHat = ratingHat;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "userId=" + userId +
                ", itemId=" + itemId +
                ", rating=" + rating +
                ", ratingHat=" + ratingHat +
                '}';
    }
}
