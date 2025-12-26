package edu.hyit.zyj.icss.dao;



import edu.hyit.zyj.icss.model.CommunityPost;
import edu.hyit.zyj.icss.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 邻里圈动态数据访问对象
 */
public class CommunityPostDao {
    
    /**
     * 创建新的邻里圈动态
     * @param post 邻里圈动态对象
     * @return 是否创建成功
     */
    public boolean createPost(CommunityPost post) {
        String sql = "INSERT INTO community_posts (user_id, content, images, visibility, like_count, comment_count) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getImages());
            stmt.setString(4, post.getVisibility());
            stmt.setInt(5, post.getLikeCount());
            stmt.setInt(6, post.getCommentCount());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // 获取生成的主键
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 根据ID查找邻里圈动态
     * @param id 动态ID
     * @return CommunityPost对象，如果未找到返回null
     */
    public CommunityPost findById(int id) {
        String sql = "SELECT * FROM community_posts WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCommunityPost(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取所有公开的邻里圈动态（分页）
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 邻里圈动态列表
     */
    public List<CommunityPost> getPublicPosts(int offset, int limit) {
        List<CommunityPost> posts = new ArrayList<>();
        String sql = "SELECT * FROM community_posts WHERE visibility = 'public' ORDER BY create_time DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                posts.add(mapResultSetToCommunityPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
    
    /**
     * 获取指定用户的所有邻里圈动态
     * @param userId 用户ID
     * @return 邻里圈动态列表
     */
    public List<CommunityPost> getPostsByUserId(int userId) {
        List<CommunityPost> posts = new ArrayList<>();
        String sql = "SELECT * FROM community_posts WHERE user_id = ? ORDER BY create_time DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                posts.add(mapResultSetToCommunityPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
    
    /**
     * 更新邻里圈动态的点赞数
     * @param postId 动态ID
     * @param likeCount 点赞数
     * @return 是否更新成功
     */
    public boolean updateLikeCount(int postId, int likeCount) {
        String sql = "UPDATE community_posts SET like_count = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, likeCount);
            stmt.setInt(2, postId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 更新邻里圈动态的评论数
     * @param postId 动态ID
     * @param commentCount 评论数
     * @return 是否更新成功
     */
    public boolean updateCommentCount(int postId, int commentCount) {
        String sql = "UPDATE community_posts SET comment_count = ?, update_time = NOW() WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commentCount);
            stmt.setInt(2, postId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 删除邻里圈动态
     * @param postId 动态ID
     * @return 是否删除成功
     */
    public boolean deletePost(int postId) {
        String sql = "DELETE FROM community_posts WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 将ResultSet映射为CommunityPost对象
     * @param rs ResultSet对象
     * @return CommunityPost对象
     * @throws SQLException
     */
    private CommunityPost mapResultSetToCommunityPost(ResultSet rs) throws SQLException {
        CommunityPost post = new CommunityPost();
        post.setId(rs.getInt("id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        post.setImages(rs.getString("images"));
        post.setVisibility(rs.getString("visibility"));
        post.setLikeCount(rs.getInt("like_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        post.setCreateTime(rs.getTimestamp("create_time"));
        post.setUpdateTime(rs.getTimestamp("update_time"));
        return post;
    }
}