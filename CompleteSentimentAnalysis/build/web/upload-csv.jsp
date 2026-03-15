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
    <title>Upload CSV - Sentiment Analysis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        .sidebar { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .sidebar a { color: white; text-decoration: none; padding: 15px; display: block; transition: 0.3s; }
        .sidebar a:hover { background: rgba(255,255,255,0.1); padding-left: 25px; }
        .sidebar a.active { background: rgba(255,255,255,0.2); border-left: 4px solid white; }
        .upload-area { border: 3px dashed #dee2e6; border-radius: 20px; padding: 40px; text-align: center; cursor: pointer; transition: 0.3s; }
        .upload-area:hover { border-color: #667eea; background: #f8f9fa; }
        .upload-area.dragover { border-color: #28a745; background: #e8f5e9; }
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
                <h2 class="mb-4">Upload CSV File</h2>
                
                <% if(request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>
                
                <div class="row">
                    <div class="col-md-8">
                        <div class="card">
                            <div class="card-body p-4">
                                <form action="feedback/upload" method="post" enctype="multipart/form-data" id="uploadForm">
                                    <div class="upload-area" id="uploadArea">
                                        <i class="bi bi-cloud-upload" style="font-size: 48px;"></i>
                                        <h4 class="mt-3">Drag & Drop CSV File Here</h4>
                                        <p class="text-muted">or click to browse</p>
                                        <input type="file" name="csvFile" id="fileInput" accept=".csv" style="display: none;">
                                        <div id="fileInfo" style="display: none;"></div>
                                    </div>
                                    
                                    <div class="progress mt-3" style="display: none;" id="progress">
                                        <div class="progress-bar progress-bar-striped progress-bar-animated" 
                                             role="progressbar" style="width: 0%">0%</div>
                                    </div>
                                    
                                    <div class="mt-3">
                                        <button type="submit" class="btn btn-primary" id="uploadBtn" disabled>
                                            <i class="bi bi-upload"></i> Upload and Analyze
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header bg-white">
                                <h5>CSV Format Guide</h5>
                            </div>
                            <div class="card-body">
                                <p>Your CSV file should have these columns:</p>
                                <table class="table table-sm">
                                    <thead>
                                        <tr>
                                            <th>Column</th>
                                            <th>Required</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>Feedback Text</td>
                                            <td><span class="text-success">Yes</span></td>
                                        </tr>
                                        <tr>
                                            <td>Source (optional)</td>
                                            <td><span class="text-muted">No</span></td>
                                        </tr>
                                    </tbody>
                                </table>
                                
                                <h6 class="mt-3">Example:</h6>
                                <pre class="bg-light p-2 small">
Feedback,Source
"Great product!",Email
"Terrible service",Twitter
"Average quality",Survey</pre>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        const uploadArea = document.getElementById('uploadArea');
        const fileInput = document.getElementById('fileInput');
        const fileInfo = document.getElementById('fileInfo');
        const uploadBtn = document.getElementById('uploadBtn');
        const progress = document.getElementById('progress');
        
        // Click upload area
        uploadArea.addEventListener('click', () => {
            fileInput.click();
        });
        
        // Drag & drop events
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.classList.add('dragover');
        });
        
        uploadArea.addEventListener('dragleave', () => {
            uploadArea.classList.remove('dragover');
        });
        
        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                fileInput.files = files;
                updateFileInfo(files[0]);
            }
        });
        
        // File selection
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                updateFileInfo(fileInput.files[0]);
            }
        });
        
        function updateFileInfo(file) {
            if (file.name.endsWith('.csv')) {
                fileInfo.innerHTML = `<div class="alert alert-success">
                    Selected: ${file.name} (${(file.size/1024).toFixed(2)} KB)
                </div>`;
                fileInfo.style.display = 'block';
                uploadBtn.disabled = false;
            } else {
                fileInfo.innerHTML = `<div class="alert alert-danger">
                    Please select a CSV file
                </div>`;
                fileInfo.style.display = 'block';
                uploadBtn.disabled = true;
            }
        }
        
        // Show progress on submit
        document.getElementById('uploadForm').addEventListener('submit', () => {
            progress.style.display = 'block';
            let width = 0;
            const interval = setInterval(() => {
                if (width >= 90) {
                    clearInterval(interval);
                } else {
                    width += 10;
                    document.querySelector('.progress-bar').style.width = width + '%';
                    document.querySelector('.progress-bar').textContent = width + '%';
                }
            }, 200);
        });
    </script>
</body>
</html>
