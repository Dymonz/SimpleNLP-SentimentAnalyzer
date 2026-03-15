<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Register - Sentiment Analysis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }
        .card { border: none; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); margin-top: 50px; }
        .card-header { background: linear-gradient(45deg, #667eea, #764ba2); color: white; border-radius: 15px 15px 0 0 !important; padding: 20px; }
        .btn-primary { background: linear-gradient(45deg, #667eea, #764ba2); border: none; }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header text-center">
                        <h3>📝 Create Account</h3>
                        <p class="mb-0">Join Sentiment Analysis System</p>
                    </div>
                    <div class="card-body p-4">
                        <% if(request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger">
                                <%= request.getAttribute("error") %>
                            </div>
                        <% } %>
                        
                        <form action="auth/register" method="post" id="registerForm">
                            <div class="mb-3">
                                <label class="form-label">Full Name</label>
                                <input type="text" name="fullName" class="form-control" required>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input type="email" name="email" class="form-control" required>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Username</label>
                                <input type="text" name="username" class="form-control" required minlength="3">
                                <small class="text-muted">Minimum 3 characters</small>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Password</label>
                                <input type="password" name="password" class="form-control" required minlength="6">
                                <small class="text-muted">Minimum 6 characters</small>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Confirm Password</label>
                                <input type="password" name="confirmPassword" class="form-control" required>
                                <small class="text-danger" id="passwordError" style="display:none;">Passwords do not match</small>
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100 py-2">Register</button>
                        </form>
                        
                        <div class="text-center mt-3">
                            <p>Already have an account? <a href="login.jsp">Login here</a></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        // Client-side validation
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            var password = document.getElementsByName('password')[0].value;
            var confirm = document.getElementsByName('confirmPassword')[0].value;
            
            if (password !== confirm) {
                e.preventDefault();
                document.getElementById('passwordError').style.display = 'block';
            }
        });
    </script>
</body>
</html>