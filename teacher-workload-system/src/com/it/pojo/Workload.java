package com.it.pojo;

//todo: 工作量模型
public class Workload {
    private String id;//工作量编号
    private String teacher;//授课教师
    private String workDate;//工作量 日期
    private float hours;//工作量 时数
    private String description;//工作描述
    private String feedback;



    public Workload() {
    }

    public Workload(String id, String teacher, String workDate,
                    float hours, String description, String feedback) {
        this.id = id;
        this.teacher = teacher;
        this.workDate = workDate;
        this.hours = hours;
        this.description = description;
        this.feedback = feedback;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public float getHours() {
        return hours;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

}