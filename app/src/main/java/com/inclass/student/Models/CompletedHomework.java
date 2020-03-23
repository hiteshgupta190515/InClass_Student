package com.inclass.student.Models;

public class CompletedHomework {

    private String id,homework_id,completed,homework_date,submission_date,description,subject_name,subjectgrp_name;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getHomework_id() {
        return homework_id;
    }
    public void setHomework_id(String homework_id) {
        this.homework_id = homework_id;
    }

    public String getCompleted() {
        return completed;
    }
    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getHomework_date() {
        return homework_date;
    }
    public void setHomework_date(String homework_date) {
        this.homework_date = homework_date;
    }

    public String getSubmission_date() {
        return submission_date;
    }
    public void setSubmission_date(String submission_date) {
        this.submission_date = submission_date;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject_name() {
        return subject_name;
    }
    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubjectgrp_name() {
        return subjectgrp_name;
    }
    public void setSubjectgrp_name(String subjectgrp_name) {
        this.subjectgrp_name = subjectgrp_name;
    }
}
