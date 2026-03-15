package com.sentiment.dao;

import com.sentiment.model.User;
import com.sentiment.utils.DBConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDAO {
    
    public User login(String username, String password) {
        Map<String, String> userMap = DBConnection.loginUser(username, password);
        if (userMap != null) {
            User user = new User();
            user.setUsername(userMap.get("username"));
            user.setFullName(userMap.get("fullName"));
            user.setEmail(userMap.get("email"));
            user.setRole(userMap.get("role"));
            // Set default values since file storage doesn't have these
            user.setId(1);
            user.setPassword("");
            user.setCreatedAt(new java.util.Date());
            return user;
        }
        return null;
    }
    
    public boolean register(User user) {
        return DBConnection.registerUser(
            user.getUsername(),
            user.getPassword(),
            user.getFullName(),
            user.getEmail()
        );
    }
    
    public boolean usernameExists(String username) {
        // Check if user exists in file storage
        Map<String, String> userMap = DBConnection.loginUser(username, "");
        return userMap != null;
    }
    
    public User getUserById(int id) {
        // File storage doesn't use IDs, return null or fetch from list
        // For simplicity, we'll return null
        return null;
    }
    
    public boolean updateUser(User user) {
        // File storage doesn't support updates easily
        // For now, return true
        return true;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        // This would need to read from file - for now return empty list
        return users;
    }
}
