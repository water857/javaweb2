package edu.hyit.zyj.icss.model;

import java.util.Date;

/**
 * 邻里圈动态实体类
 */
public class CommunityPost {
    private Integer id;
    private Integer userId;
    private String content;
    private String images; // JSON格式存储图片URL数组
    private String visibility; // public, neighbors_only, private
    private Integer likeCount;
    private Integer commentCount;
    private Date createTime;
    private Date updateTime;

    // 构造函数
    public CommunityPost() {
        this.likeCount = 0;
        this.commentCount = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "CommunityPost{" +
                "id=" + id +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", visibility='" + visibility + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}