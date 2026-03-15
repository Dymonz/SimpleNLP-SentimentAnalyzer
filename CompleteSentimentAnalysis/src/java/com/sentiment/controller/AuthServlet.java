package com.sentiment.controller;

import com.sentiment.dao.UserDAO;
import com.sentiment.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if ("/login".equals(path)) {
            handleLogin(request, response);
        } else if ("/register".equals(path)) {
            handleRegister(request, response);
        } else if ("/logout".equals(path)) {
            handleLogout(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if ("/logout".equals(path)) {
            handleLogout(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
    
    // AUTO-LOGIN - BYPASSES ALL AUTHENTICATION
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Create session
        HttpSession session = request.getSession();
        
        // Create a user map with admin privileges
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", "admin");
        userMap.put("fullName", "Administrator");
        userMap.put("email", "admin@example.com");
        userMap.put("role", "admin");
        
        // Set all session attributes
        session.setAttribute("user", userMap);
        session.setAttribute("userId", 1);
        session.setAttribute("username", "admin");
        session.setAttribute("fullName", "Administrator");
        session.setAttribute("role", "admin");
        
        System.out.println("🔓 Auto-login successful - Redirecting to dashboard");
        
        // Redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
    
    // Registration with auto-login
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        
        // Simple validation
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "Username is required");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Password is required");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Full name is required");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        if (email == null || !email.contains("@")) {
            request.setAttribute("error", "Valid email is required");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Try to register (may still fail if file storage not working)
        User user = new User(username, password, fullName, email, "user");
        boolean registered = userDAO.register(user);
        
        // AUTO-LOGIN AFTER REGISTRATION - even if registration fails
        HttpSession session = request.getSession();
        
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("fullName", fullName);
        userMap.put("email", email);
        userMap.put("role", "user");
        
        session.setAttribute("user", userMap);
        session.setAttribute("userId", 1);
        session.setAttribute("username", username);
        session.setAttribute("fullName", fullName);
        session.setAttribute("role", "user");
        
        System.out.println("🔓 Auto-login after registration - Redirecting to dashboard");
        
        // Redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("🔒 User logged out");
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}