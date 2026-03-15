package com.sentiment.controller;

import com.sentiment.dao.FeedbackDAO;
import com.sentiment.model.Feedback;
import com.sentiment.service.SentimentService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    private SentimentService sentimentService;
    
    @Override
    public void init() {
        sentimentService = new SentimentService();
        getServletContext().setAttribute("sentimentService", sentimentService);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        // Get statistics
        int[] counts = feedbackDAO.getSentimentCounts(userId, role);
        List<Feedback> recentFeedback = feedbackDAO.getAllFeedback(userId, role);
        
        // Limit to 10 most recent
        if (recentFeedback.size() > 10) {
            recentFeedback = recentFeedback.subList(0, 10);
        }
        
        request.setAttribute("totalCount", counts[0]);
        request.setAttribute("positiveCount", counts[1]);
        request.setAttribute("negativeCount", counts[2]);
        request.setAttribute("neutralCount", counts[3]);
        request.setAttribute("recentFeedback", recentFeedback);
        request.setAttribute("lexiconSize", sentimentService.getLexiconSize());
        
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
