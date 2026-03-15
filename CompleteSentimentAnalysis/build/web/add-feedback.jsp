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
    <title>Add Feedback - Sentiment Analysis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        .sidebar { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .sidebar a { color: white; text-decoration: none; padding: 15px; display: block; transition: 0.3s; }
        .sidebar a:hover { background: rgba(255,255,255,0.1); padding-left: 25px; }
        .sidebar a.active { background: rgba(255,255,255,0.2); border-left: 4px solid white; }
        .card { border: none; border-radius: 15px; box-shadow: 0 5px 20px rgba(0,0,0,0.1); }
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
                <a href="feedback/add" class="active"><i class="bi bi-plus-circle"></i> Add Feedback</a>
                <a href="feedback/upload"><i class="bi bi-upload"></i> Upload CSV</a>
                <a href="profile.jsp"><i class="bi bi-person"></i> Profile</a>
                <a href="auth/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 p-4">
                <h2 class="mb-4">Add New Feedback</h2>
                
                <div class="row">
                    <div class="col-md-8">
                        <div class="card">
                            <div class="card-body p-4">
                                <% if(request.getAttribute("error") != null) { %>
                                    <div class="alert alert-danger">
                                        <%= request.getAttribute("error") %>
                                    </div>
                                <% } %>
                                
                                <form action="feedback/add" method="post">
                                    <div class="mb-3">
                                        <label class="form-label">Feedback Text</label>
                                        <textarea name="feedback" class="form-control" rows="5" required 
                                                  placeholder="Enter customer feedback here..."></textarea>
                                        <small class="text-muted">Enter the feedback text to analyze</small>
                                    </div>
                                    
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Source (Optional)</label>
                                            <input type="text" name="source" class="form-control" 
                                                   placeholder="e.g., Email, Twitter, Survey">
                                        </div>
                                        
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Feedback Date</label>
                                            <input type="date" name="feedbackDate" class="form-control" 
                                                   value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                                        </div>
                                    </div>
                                    
                                    <div class="alert alert-info">
                                        <i class="bi bi-info-circle"></i>
                                        The sentiment will be automatically analyzed when you submit.
                                    </div>
                                    
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-magic"></i> Analyze & Save
                                    </button>
                                    <a href="feedback/list" class="btn btn-secondary">Cancel</a>
                                </form>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header bg-white">
                                <h5>Tips for Good Feedback</h5>
                            </div>
                            <div class="card-body">
                                <ul class="list-unstyled">
                                    <li class="mb-2">✓ Be specific about what you liked/disliked</li>
                                    <li class="mb-2">✓ Include both positive and negative points</li>
                                    <li class="mb-2">✓ Mention specific features or aspects</li>
                                    <li class="mb-2">✓ Use natural language</li>
                                </ul>
                                
                                <hr>
                                
                                <h6>Example:</h6>
                                <p class="text-muted">
                                    "The customer service was excellent and very helpful, 
                                    but the delivery was slower than expected."
                                </p>
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
