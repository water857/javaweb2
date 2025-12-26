package edu.hyit.zyj.icss.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hyit.zyj.icss.util.ApplicationConfiguration;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传Servlet
 */
@WebServlet("/api/upload")
public class FileUploadServlet extends HttpServlet {
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        // 检查是否为multipart/form-data请求
        if (!ServletFileUpload.isMultipartContent(request)) {
            sendErrorResponse(response, "请求格式不正确，必须为multipart/form-data", 400);
            return;
        }
        
        // 配置上传设置
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(ApplicationConfiguration.getMaxFileSize());
        upload.setSizeMax(ApplicationConfiguration.getMaxRequestSize());
        
        // 获取服务器上的上传目录绝对路径
        String uploadPath = getServletContext().getRealPath("") + File.separator + ApplicationConfiguration.getUploadDirectory();
        
        // 创建上传目录（如果不存在）
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        try {
            // 解析请求获取文件项
            List<FileItem> formItems = upload.parseRequest(request);
            
            if (formItems != null && !formItems.isEmpty()) {
                for (FileItem item : formItems) {
                    // 处理文件字段
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        // 生成唯一文件名
                        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                        String filePath = uploadPath + File.separator + uniqueFileName;
                        
                        File storeFile = new File(filePath);
                        
                        // 保存文件到磁盘
                        item.write(storeFile);
                        
                        // 返回文件访问URL
                        String fileUrl = request.getContextPath() + "/" + ApplicationConfiguration.getUploadDirectory() + "/" + uniqueFileName;
                        
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("message", "文件上传成功");
                        result.put("fileName", fileName);
                        result.put("fileUrl", fileUrl);
                        sendJsonResponse(response, result);
                        return;
                    }
                }
            }
            
            sendErrorResponse(response, "未找到上传文件", 400);
        } catch (Exception ex) {
            sendErrorResponse(response, "文件上传失败: " + ex.getMessage(), 500);
        }
    }
    
    /**
     * 发送JSON响应
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), data);
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        objectMapper.writeValue(response.getWriter(), error);
    }
}