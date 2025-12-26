package edu.hyit.zyj.icss.model;

import java.util.Date;

/**
 * 报修工单实体类
 */
public class RepairOrder {
    private Integer id;
    private String orderNumber;
    private Integer residentId;
    private String title;
    private String description;
    private String images; // JSON格式存储图片URL数组
    private String category; // electrical, plumbing, elevator, other
    private String urgencyLevel; // low, medium, high, emergency
    private String status; // submitted, assigned, in_progress, completed, cancelled
    private Integer assignedServiceId;
    private Date appointmentTime;
    private Date completedTime;
    private Integer rating;
    private String comment;
    private Date createTime;
    private Date updateTime;

    // 构造函数
    public RepairOrder() {
        this.status = "submitted";
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getResidentId() {
        return residentId;
    }

    public void setResidentId(Integer residentId) {
        this.residentId = residentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updateTime = new Date();
    }

    public Integer getAssignedServiceId() {
        return assignedServiceId;
    }

    public void setAssignedServiceId(Integer assignedServiceId) {
        this.assignedServiceId = assignedServiceId;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        return "RepairOrder{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", residentId=" + residentId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", urgencyLevel='" + urgencyLevel + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}