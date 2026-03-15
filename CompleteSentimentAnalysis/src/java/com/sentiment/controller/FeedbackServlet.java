package com.sentiment.controller;

import com.sentiment.dao.FeedbackDAO;
import com.sentiment.model.Feedback;
import com.sentiment.service.SentimentService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/feedback/*")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // 10MB
public class FeedbackServlet extends HttpServlet {
    
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if ("/list".equals(path) || path == null) {
            showFeedbackList(request, response);
        } else if ("/add".equals(path)) {
            request.getRequestDispatcher("/add-feedback.jsp").forward(request, response);
        } else if ("/upload".equals(path)) {
            request.getRequestDispatcher("/upload-csv.jsp").forward(request, response);
        } else if ("/delete".equals(path)) {
            deleteFeedback(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if ("/add".equals(path)) {
            addFeedback(request, response);
        } else if ("/upload".equals(path)) {
            handleCSVUpload(request, response);
        } else if ("/search".equals(path)) {
            searchFeedback(request, response);
        }
    }
    
    private void showFeedbackList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        String keyword = request.getParameter("keyword");
        String sentiment = request.getParameter("sentiment");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        
        List<Feedback> feedbackList;
        
        if (keyword != null || (sentiment != null && !sentiment.isEmpty()) || 
            dateFrom != null || dateTo != null) {
            
            feedbackList = feedbackDAO.searchFeedback(userId, role, keyword, sentiment, dateFrom, dateTo);
        } else {
            feedbackList = feedbackDAO.getAllFeedback(userId, role);
        }
        
        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("totalResults", feedbackList.size());
        request.getRequestDispatcher("/feedback-list.jsp").forward(request, response);
    }
    
    private void addFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        String feedbackText = request.getParameter("feedback");
        String source = request.getParameter("source");
        String dateStr = request.getParameter("feedbackDate");
        
        // Validate
        if (feedbackText == null || feedbackText.trim().isEmpty()) {
            request.setAttribute("error", "Feedback text is required");
            request.getRequestDispatcher("/add-feedback.jsp").forward(request, response);
            return;
        }
        
        // Analyze sentiment
        SentimentService service = (SentimentService) getServletContext().getAttribute("sentimentService");
        Map<String, Object> result = service.analyze(feedbackText);
        
        // Create feedback object
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setFeedbackText(feedbackText);
        feedback.setSentiment((String) result.get("sentiment"));
        feedback.setConfidence((double) result.get("confidence"));
        feedback.setSource(source != null ? source : "manual");
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            feedback.setFeedbackDate(dateStr != null ? sdf.parse(dateStr) : new Date());
        } catch (Exception e) {
            feedback.setFeedbackDate(new Date());
        }
        
        // Save
        boolean success = feedbackDAO.addFeedback(feedback);
        
        if (success) {
            session.setAttribute("message", "Feedback added successfully!");
            response.sendRedirect(request.getContextPath() + "/feedback/list");
        } else {
            request.setAttribute("error", "Failed to add feedback");
            request.getRequestDispatcher("/add-feedback.jsp").forward(request, response);
        }
    }
    
    private void handleCSVUpload(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        Part filePart = request.getPart("csvFile");
        
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("error", "Please select a CSV file");
            request.getRequestDispatcher("/upload-csv.jsp").forward(request, response);
            return;
        }
        
        SentimentService service = (SentimentService) getServletContext().getAttribute("sentimentService");
        
        int success = 0;
        int failed = 0;
        int total = 0;
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                total++;
                
                // Skip header row if needed
                if (isFirstLine) {
                    isFirstLine = false;
                    // Check if first line looks like headers
                    if (line.toLowerCase().contains("feedback") || 
                        line.toLowerCase().contains("text") ||
                        line.toLowerCase().contains("comment")) {
                        continue;
                    }
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    String feedbackText = parts[0].trim();
                    String source = parts.length > 1 ? parts[1].trim() : "CSV Upload";
                    
                    if (!feedbackText.isEmpty()) {
                        // Analyze sentiment
                        Map<String, Object> result = service.analyze(feedbackText);
                        
                        // Create and save feedback
                        Feedback feedback = new Feedback();
                        feedback.setUserId(userId);
                        feedback.setFeedbackText(feedbackText);
                        feedback.setSentiment((String) result.get("sentiment"));
                        feedback.setConfidence((double) result.get("confidence"));
                        feedback.setSource(source);
                        feedback.setFeedbackDate(new Date());
                        
                        if (feedbackDAO.addFeedback(feedback)) {
                            success++;
                        } else {
                            failed++;
                        }
                    } else {
                        failed++;
                    }
                } else {
                    failed++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            failed = total - success;
        }
        
        request.setAttribute("success", success);
        request.setAttribute("failed", failed);
        request.setAttribute("total", total);
        request.getRequestDispatcher("/upload-result.jsp").forward(request, response);
    }
    
    private void searchFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        String keyword = request.getParameter("keyword");
        String sentiment = request.getParameter("sentiment");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        
        List<Feedback> results = feedbackDAO.searchFeedback(userId, role, keyword, sentiment, dateFrom, dateTo);
        
        // For AJAX requests, return JSON
        String ajax = request.getParameter("ajax");
        if ("true".equals(ajax)) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            
            out.print("[");
            for (int i = 0; i < results.size(); i++) {
                Feedback f = results.get(i);
                out.print("{");
                out.print("\"id\":\"" + f.getId() + "\",");
                out.print("\"feedback\":\"" + escapeJson(f.getFeedbackText()) + "\",");
                out.print("\"sentiment\":\"" + f.getSentiment() + "\",");
                out.print("\"confidence\":\"" + String.format("%.2f", f.getConfidence()) + "\",");
                out.print("\"username\":\"" + f.getUsername() + "\",");
                out.print("\"date\":\"" + new SimpleDateFormat("yyyy-MM-dd").format(f.getFeedbackDate()) + "\"");
                out.print("}");
                if (i < results.size() - 1) out.print(",");
            }
            out.print("]");
        } else {
            // Regular request
            request.setAttribute("feedbackList", results);
            request.setAttribute("totalResults", results.size());
            request.getRequestDispatcher("/feedback-list.jsp").forward(request, response);
        }
    }
    
    private void deleteFeedback(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            session.setAttribute("error", "Feedback ID is required");
            response.sendRedirect(request.getContextPath() + "/feedback/list");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            boolean deleted = feedbackDAO.deleteFeedback(id, userId, role);
            
            if (deleted) {
                session.setAttribute("message", "Feedback deleted successfully!");
            } else {
                session.setAttribute("error", "Failed to delete feedback");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Invalid feedback ID");
        }
        
        response.sendRedirect(request.getContextPath() + "/feedback/list");
    }
    
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
