<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if(session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    Map<String, String> user = (Map<String, String>) session.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Profile - Sentiment Analysis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        .sidebar { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .sidebar a { color: white; text-decoration: none; padding: 15px; display: block; transition: 0.3s; }
        .sidebar a:hover { background: rgba(255,255,255,0.1); padding-left: 25px; }
        .sidebar a.active { background: rgba(255,255,255,0.2); border-left: 4px solid white; }
        .profile-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px; border-radius: 15px; }
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
                <a href="feedback/upload"><i class="bi bi-upload"></i> Upload CSV</a>
                <a href="profile.jsp" class="active"><i class="bi bi-person"></i> Profile</a>
                <a href="auth/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 p-4">
                <h2 class="mb-4">My Profile</h2>
                
                <% if(request.getAttribute("message") != null) { %>
                    <div class="alert alert-success">
                        <%= request.getAttribute("message") %>
                    </div>
                <% } %>
                <% if(request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>
                
                <div class="row">
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-body text-center">
                                <div class="profile-header mb-4">
                                    <i class="bi bi-person-circle" style="font-size: 80px;"></i>
                                    <h3 class="mt-3"><%= user.get("fullName") %></h3>
                                    <p><%= user.get("role") %></p>
                                </div>
                                
                                <div class="text-start">
                                    <p><i class="bi bi-person"></i> <strong>Username:</strong> <%= user.get("username") %></p>
                                    <p><i class="bi bi-envelope"></i> <strong>Email:</strong> <%= user.get("email") %></p>
                                    <p><i class="bi bi-calendar"></i> <strong>Member since:</strong> 2024</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-8">
                        <div class="card">
                            <div class="card-header bg-white">
                                <h5>Edit Profile</h5>
                            </div>
                            <div class="card-body">
                                <form action="profile" method="post">
                                    <div class="mb-3">
                                        <label class="form-label">Full Name</label>
                                        <input type="text" name="fullName" class="form-control" 
                                               value="<%= user.get("fullName") %>" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label">Email</label>
                                        <input type="email" name="email" class="form-control" 
                                               value="<%= user.get("email") %>" required>
                                    </div>
                                    
                                    <hr>
                                    
                                    <div class="mb-3">
                                        <label class="form-label">Current Password</label>
                                        <input type="password" name="currentPassword" class="form-control">
                                        <small class="text-muted">Required to save changes</small>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label">New Password (optional)</label>
                                        <input type="password" name="newPassword" class="form-control">
                                    </div>
                                    
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-save"></i> Save Changes
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
