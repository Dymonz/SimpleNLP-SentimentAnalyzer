package com.sentiment.utils;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class DBConnection {
    private static final String DATA_DIR = System.getProperty("user.home") + "/sentiment_data/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String FEEDBACK_FILE = DATA_DIR + "feedback.txt";
    
    static {
        // Create data directory if it doesn't exist
        new File(DATA_DIR).mkdirs();
        initFiles();
    }
    
    private static void initFiles() {
        try {
            // Create users file with default admin
            File uFile = new File(USERS_FILE);
            if (!uFile.exists()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(uFile))) {
                    // username:admin, password:admin123 (hashed), role:admin
                    pw.println("admin|8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918|Administrator|admin@example.com|admin");
                }
            }
            
            // Create feedback file
            File fFile = new File(FEEDBACK_FILE);
            if (!fFile.exists()) {
                fFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ==================== USER METHODS ====================
    
    public static Map<String, String> loginUser(String username, String password) {
        String hashedPassword = hashPassword(username + password);
        
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5 && parts[0].equals(username) && parts[1].equals(hashedPassword)) {
                    Map<String, String> user = new HashMap<>();
                    user.put("username", parts[0]);
                    user.put("fullName", parts[2]);
                    user.put("email", parts[3]);
                    user.put("role", parts[4]);
                    return user;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean registerUser(String username, String password, String fullName, String email) {
        String hashedPassword = hashPassword(username + password);
        
        // Check if user exists
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return false; // User already exists
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Add new user
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            pw.println(username + "|" + hashedPassword + "|" + fullName + "|" + email + "|user");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== FEEDBACK METHODS ====================
    
    public static void saveFeedback(int userId, String username, String feedback, String sentiment, 
                                   double confidence, String source) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String id = UUID.randomUUID().toString().substring(0, 8);
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(FEEDBACK_FILE, true))) {
            // id|timestamp|userId|username|feedback|sentiment|confidence|source
            pw.println(id + "|" + timestamp + "|" + userId + "|" + username + "|" + 
                      feedback.replace("|", "-") + "|" + sentiment + "|" + 
                      confidence + "|" + (source != null ? source : "manual"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Map<String, String>> getAllFeedback(String username, String role) {
        List<Map<String, String>> feedbackList = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    // Admin sees all, users see only theirs
                    if (role.equals("admin") || parts[3].equals(username)) {
                        Map<String, String> fb = new HashMap<>();
                        fb.put("id", parts[0]);
                        fb.put("timestamp", parts[1]);
                        fb.put("userId", parts[2]);
                        fb.put("username", parts[3]);
                        fb.put("feedback", parts[4]);
                        fb.put("sentiment", parts[5]);
                        fb.put("confidence", parts[6]);
                        fb.put("source", parts[7]);
                        feedbackList.add(fb);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Sort by most recent
        feedbackList.sort((a, b) -> b.get("timestamp").compareTo(a.get("timestamp")));
        return feedbackList;
    }
    
    public static Map<String, Integer> getSentimentCounts(String username, String role) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("positive", 0);
        counts.put("negative", 0);
        counts.put("neutral", 0);
        counts.put("total", 0);
        
        List<Map<String, String>> allFeedback = getAllFeedback(username, role);
        
        for (Map<String, String> fb : allFeedback) {
            String sentiment = fb.get("sentiment");
            counts.put(sentiment, counts.getOrDefault(sentiment, 0) + 1);
            counts.put("total", counts.get("total") + 1);
        }
        
        return counts;
    }
    
    public static boolean deleteFeedback(String id, String username, String role) {
        List<String> lines = new ArrayList<>();
        boolean deleted = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4 && parts[0].equals(id)) {
                    // Check if user can delete
                    if (role.equals("admin") || parts[3].equals(username)) {
                        deleted = true;
                        continue; // Skip this line
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (deleted) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(FEEDBACK_FILE))) {
                for (String line : lines) {
                    pw.println(line);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    private static String hashPassword(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return input;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        System.out.println("✅ File storage ready at: " + DATA_DIR);
        System.out.println("Default admin: admin / admin123");
    }
}