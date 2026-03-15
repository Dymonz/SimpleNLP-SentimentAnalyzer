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
    <title>Upload Results - Sentiment Analysis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        .sidebar { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .sidebar a { color: white; text-decoration: none; padding: 15px; display: block; transition: 0.3s; }
        .result-card { border-radius: 15px; padding: 30px; text-align: center; }
        .success-icon { font-size: 64px; color: #28a745; }
        .partial-icon { font-size: 64px; color: #ffc107; }
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
                <a href="feedback/list"><i class="bi bi-chat-dots"></i> Feedback List</a>
                <a href="feedback/add"><i class="bi bi-plus-circle"></i> Add Feedback</a>
                <a href="feedback/upload" class="active"><i class="bi bi-upload"></i> Upload CSV</a>
                <a href="profile.jsp"><i class="bi bi-person"></i> Profile</a>
                <a href="auth/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 p-4">
                <div class="row justify-content-center">
                    <div class="col-md-6">
                        <div class="card result-card">
                            <div class="card-body text-center">
                                <% 
                                    int success = (Integer) request.getAttribute("success");
                                    int failed = (Integer) request.getAttribute("failed");
                                    int total = (Integer) request.getAttribute("total");
                                %>
                                
                                <% if(failed == 0) { %>
                                    <i class="bi bi-check-circle-fill success-icon"></i>
                                    <h2 class="mt-3">Upload Complete!</h2>
                                    <p class="lead">All <%= total %> feedback entries processed successfully</p>
                                <% } else if(success > 0) { %>
                                    <i class="bi bi-exclamation-triangle-fill partial-icon"></i>
                                    <h2 class="mt-3">Upload Completed with Issues</h2>
                                    <p class="lead"><%= success %> of <%= total %> entries processed successfully</p>
                                <% } else { %>
                                    <i class="bi bi-x-circle-fill" style="font-size: 64px; color: #dc3545;"></i>
                                    <h2 class="mt-3">Upload Failed</h2>
                                    <p class="lead">No entries could be processed</p>
                                <% } %>
                                
                                <hr>
                                
                                <div class="row mt-4">
                                    <div class="col-4">
                                        <h3 class="text-success"><%= success %></h3>
                                        <p class="text-muted">Successful</p>
                                    </div>
                                    <div class="col-4">
                                        <h3 class="text-danger"><%= failed %></h3>
                                        <p class="text-muted">Failed</p>
                                    </div>
                                    <div class="col-4">
                                        <h3><%= total %></h3>
                                        <p class="text-muted">Total</p>
                                    </div>
                                </div>
                                
                                <div class="mt-4">
                                    <a href="feedback/list" class="btn btn-primary">
                                        <i class="bi bi-list"></i> View All Feedback
                                    </a>
                                    <a href="feedback/upload" class="btn btn-secondary">
                                        <i class="bi bi-upload"></i> Upload Another File
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
