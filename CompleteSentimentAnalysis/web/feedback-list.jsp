<%@page import="java.util.List"%>
<%@page import="com.sentiment.model.Feedback"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if(session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Feedback List - Sentiment Analysis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        .sidebar { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .sidebar a { color: white; text-decoration: none; padding: 15px; display: block; transition: 0.3s; }
        .sidebar a:hover { background: rgba(255,255,255,0.1); padding-left: 25px; }
        .sidebar a.active { background: rgba(255,255,255,0.2); border-left: 4px solid white; }
        .badge-positive { background: #28a745; color: white; padding: 5px 10px; border-radius: 20px; }
        .badge-negative { background: #dc3545; color: white; padding: 5px 10px; border-radius: 20px; }
        .badge-neutral { background: #6c757d; color: white; padding: 5px 10px; border-radius: 20px; }
        .filter-card { background: #f8f9fa; border-radius: 10px; padding: 20px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-2 p-0 sidebar">
                <div class="p-4 text-white text-center">
                    <h4>📊 Sentiment Analysis</h4>
                    <hr>
                </div>
                <a href="dashboard"><i class="bi bi-house-door"></i> Dashboard</a>
                <a href="feedback/list" class="active"><i class="bi bi-chat-dots"></i> Feedback List</a>
                <a href="feedback/add"><i class="bi bi-plus-circle"></i> Add Feedback</a>
                <a href="feedback/upload"><i class="bi bi-upload"></i> Upload CSV</a>
                <a href="profile.jsp"><i class="bi bi-person"></i> Profile</a>
                <a href="auth/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Feedback Management</h2>
                    <div>
                        <a href="feedback/add" class="btn btn-primary">
                            <i class="bi bi-plus-circle"></i> Add New
                        </a>
                    </div>
                </div>
                
                <!-- Filter Section -->
                <div class="filter-card">
                    <h5>Filter Feedback</h5>
                    <form action="feedback/list" method="get" class="row g-3">
                        <div class="col-md-3">
                            <input type="text" name="keyword" class="form-control" placeholder="Search keyword...">
                        </div>
                        <div class="col-md-2">
                            <select name="sentiment" class="form-control">
                                <option value="">All Sentiments</option>
                                <option value="positive">Positive</option>
                                <option value="negative">Negative</option>
                                <option value="neutral">Neutral</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <input type="date" name="dateFrom" class="form-control" placeholder="From">
                        </div>
                        <div class="col-md-2">
                            <input type="date" name="dateTo" class="form-control" placeholder="To">
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-search"></i> Apply Filters
                            </button>
                            <a href="feedback/list" class="btn btn-secondary">Reset</a>
                        </div>
                    </form>
                </div>
                
                <!-- Results Count -->
                <div class="mb-3">
                    <strong><%= request.getAttribute("totalResults") %></strong> feedback entries found
                </div>
                
                <!-- Feedback Table -->
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Feedback</th>
                                <th>Sentiment</th>
                                <th>Confidence</th>
                                <th>User</th>
                                <th>Date</th>
                                <th>Source</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% List<Feedback> feedbackList = (List<Feedback>) request.getAttribute("feedbackList"); %>
                            <% if(feedbackList != null && !feedbackList.isEmpty()) { %>
                                <% for(Feedback f : feedbackList) { %>
                                    <tr>
                                        <td><%= f.getId() %></td>
                                        <td><%= f.getFeedbackText().length() > 50 ? f.getFeedbackText().substring(0,50) + "..." : f.getFeedbackText() %></td>
                                        <td>
                                            <span class="badge-<%= f.getSentiment() %>">
                                                <%= f.getSentiment() %>
                                            </span>
                                        </td>
                                        <td><%= String.format("%.2f", f.getConfidence()) %></td>
                                        <td><%= f.getUsername() %></td>
                                        <td><%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(f.getFeedbackDate()) %></td>
                                        <td><%= f.getSource() %></td>
                                        <td>
                                            <a href="feedback/edit?id=<%= f.getId() %>" class="btn btn-sm btn-warning">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="feedback/delete?id=<%= f.getId() %>" class="btn btn-sm btn-danger" 
                                               onclick="return confirm('Are you sure you want to delete this feedback?')">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="8" class="text-center">No feedback found</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
