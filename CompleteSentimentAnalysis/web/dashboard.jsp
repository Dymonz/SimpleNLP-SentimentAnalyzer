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
    <title>Dashboard - Sentiment Analysis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .sidebar { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .sidebar a { color: white; text-decoration: none; padding: 15px; display: block; transition: 0.3s; }
        .sidebar a:hover { background: rgba(255,255,255,0.1); padding-left: 25px; }
        .sidebar a.active { background: rgba(255,255,255,0.2); border-left: 4px solid white; }
        .card { border: none; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }
        .card-positive { background: linear-gradient(45deg, #28a745, #20c997); color: white; }
        .card-negative { background: linear-gradient(45deg, #dc3545, #e74c3c); color: white; }
        .card-neutral { background: linear-gradient(45deg, #6c757d, #95a5a6); color: white; }
        .badge-positive { background: #28a745; color: white; padding: 5px 10px; border-radius: 20px; }
        .badge-negative { background: #dc3545; color: white; padding: 5px 10px; border-radius: 20px; }
        .badge-neutral { background: #6c757d; color: white; padding: 5px 10px; border-radius: 20px; }
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
                <a href="dashboard" class="active"><i class="bi bi-house-door"></i> Dashboard</a>
                <a href="feedback/list"><i class="bi bi-chat-dots"></i> Feedback List</a>
                <a href="feedback/add"><i class="bi bi-plus-circle"></i> Add Feedback</a>
                <a href="feedback/upload"><i class="bi bi-upload"></i> Upload CSV</a>
                <a href="profile.jsp"><i class="bi bi-person"></i> Profile</a>
                <a href="auth/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Dashboard</h2>
                    <div>
                        Welcome, <strong><%= session.getAttribute("fullName") %></strong>
                    </div>
                </div>
                
                <!-- Alert Messages -->
                <% if(session.getAttribute("message") != null) { %>
                    <div class="alert alert-success alert-dismissible fade show">
                        <%= session.getAttribute("message") %>
                        <% session.removeAttribute("message"); %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                <% if(session.getAttribute("error") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show">
                        <%= session.getAttribute("error") %>
                        <% session.removeAttribute("error"); %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                
                <!-- Stats Cards -->
                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="card card-positive">
                            <div class="card-body">
                                <h5>Total Feedback</h5>
                                <h2><%= request.getAttribute("totalCount") %></h2>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card card-positive">
                            <div class="card-body">
                                <h5>Positive</h5>
                                <h2><%= request.getAttribute("positiveCount") %></h2>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card card-negative">
                            <div class="card-body">
                                <h5>Negative</h5>
                                <h2><%= request.getAttribute("negativeCount") %></h2>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card card-neutral">
                            <div class="card-body">
                                <h5>Neutral</h5>
                                <h2><%= request.getAttribute("neutralCount") %></h2>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Charts and Recent Feedback -->
                <div class="row">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header bg-white">
                                <h5>Sentiment Distribution</h5>
                            </div>
                            <div class="card-body">
                                <canvas id="sentimentChart"></canvas>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header bg-white d-flex justify-content-between">
                                <h5>Recent Feedback</h5>
                                <a href="feedback/list" class="btn btn-sm btn-primary">View All</a>
                            </div>
                            <div class="card-body">
                                <div class="list-group">
                                    <% List<Feedback> recent = (List<Feedback>) request.getAttribute("recentFeedback"); %>
                                    <% if(recent != null && !recent.isEmpty()) { %>
                                        <% for(Feedback f : recent) { %>
                                            <div class="list-group-item">
                                                <div class="d-flex justify-content-between">
                                                    <div>
                                                        <p class="mb-1"><%= f.getFeedbackText().length() > 50 ? f.getFeedbackText().substring(0,50) + "..." : f.getFeedbackText() %></p>
                                                        <small>by <%= f.getUsername() %> on <%= new java.text.SimpleDateFormat("MMM dd, yyyy").format(f.getFeedbackDate()) %></small>
                                                    </div>
                                                    <div>
                                                        <span class="badge-<%= f.getSentiment() %>">
                                                            <%= f.getSentiment() %>
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                        <% } %>
                                    <% } else { %>
                                        <p class="text-muted">No feedback yet</p>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Initialize chart
        var ctx = document.getElementById('sentimentChart').getContext('2d');
        var chart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: ['Positive', 'Negative', 'Neutral'],
                datasets: [{
                    data: [
                        <%= request.getAttribute("positiveCount") %>,
                        <%= request.getAttribute("negativeCount") %>,
                        <%= request.getAttribute("neutralCount") %>
                    ],
                    backgroundColor: ['#28a745', '#dc3545', '#6c757d']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });
    </script>
</body>
</html>