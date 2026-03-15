package com.sentiment.dao;

import com.sentiment.model.Feedback;
import com.sentiment.utils.DBConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeedbackDAO {
    
    public boolean addFeedback(Feedback feedback) {
        try {
            DBConnection.saveFeedback(
                feedback.getUserId(),
                feedback.getUsername(),
                feedback.getFeedbackText(),
                feedback.getSentiment(),
                feedback.getConfidence(),
                feedback.getSource()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Feedback> getAllFeedback(int userId, String role) {
        List<Feedback> feedbackList = new ArrayList<>();
        String username = getUsernameFromUserId(userId, role);
        
        List<Map<String, String>> feedbackMaps = DBConnection.getAllFeedback(username, role);
        
        for (Map<String, String> map : feedbackMaps) {
            Feedback f = new Feedback();
            f.setId(Integer.parseInt(map.get("id").hashCode() + "")); // Simple hash as ID
            f.setUserId(Integer.parseInt(map.get("userId")));
            f.setUsername(map.get("username"));
            f.setFeedbackText(map.get("feedback"));
            f.setSentiment(map.get("sentiment"));
            f.setConfidence(Double.parseDouble(map.get("confidence")));
            f.setSource(map.get("source"));
            
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                f.setFeedbackDate(sdf.parse(map.get("timestamp")));
                f.setCreatedAt(sdf.parse(map.get("timestamp")));
            } catch (Exception e) {
                f.setFeedbackDate(new java.util.Date());
                f.setCreatedAt(new java.util.Date());
            }
            
            feedbackList.add(f);
        }
        
        return feedbackList;
    }
    
    public List<Feedback> searchFeedback(int userId, String role, String keyword, 
                                        String sentiment, String dateFrom, String dateTo) {
        List<Feedback> allFeedback = getAllFeedback(userId, role);
        List<Feedback> filtered = new ArrayList<>();
        
        for (Feedback f : allFeedback) {
            boolean matches = true;
            
            if (keyword != null && !keyword.isEmpty()) {
                matches = matches && f.getFeedbackText().toLowerCase().contains(keyword.toLowerCase());
            }
            
            if (sentiment != null && !sentiment.isEmpty() && !sentiment.equals("all")) {
                matches = matches && f.getSentiment().equals(sentiment);
            }
            
            // Add date filtering if needed
            if (matches) {
                filtered.add(f);
            }
        }
        
        return filtered;
    }
    
    public boolean deleteFeedback(int id, int userId, String role) {
        String idStr = String.valueOf(id);
        String username = getUsernameFromUserId(userId, role);
        return DBConnection.deleteFeedback(idStr, username, role);
    }
    
    public int[] getSentimentCounts(int userId, String role) {
        String username = getUsernameFromUserId(userId, role);
        Map<String, Integer> counts = DBConnection.getSentimentCounts(username, role);
        
        int[] result = new int[4];
        result[0] = counts.get("total");      // total
        result[1] = counts.get("positive");   // positive
        result[2] = counts.get("negative");   // negative
        result[3] = counts.get("neutral");    // neutral
        
        return result;
    }
    
    public Feedback getFeedbackById(int feedbackId) {
        // File storage doesn't support direct ID lookup easily
        // You could implement this by scanning all feedback
        return null;
    }
    
    public boolean updateFeedback(Feedback feedback) {
        // File storage doesn't support updates easily
        // For now, return true
        return true;
    }
    
    private String getUsernameFromUserId(int userId, String role) {
        // In file storage, we don't have user IDs mapped to usernames easily
        // For simplicity, if role is admin, return empty string to get all
        if (role.equals("admin")) {
            return "";
        }
        // You'd need to look up username from user ID
        // For now, return empty
        return "";
    }
}
