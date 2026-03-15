package com.sentiment.model;

public class Feedback {
    private int id;
    private int userId;
    private String username;
    private String feedbackText;
    private String sentiment;
    private double confidence;
    private String source;
    private java.util.Date feedbackDate;
    private java.util.Date createdAt;
    
    // Getters and Setters (generate all)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public java.util.Date getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(java.util.Date feedbackDate) { this.feedbackDate = feedbackDate; }
    
    public java.util.Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }
}
