package edu.hyit.zyj.icss.service;



import edu.hyit.zyj.icss.dao.CommunityPostDao;
import edu.hyit.zyj.icss.model.CommunityPost;
import edu.hyit.zyj.icss.util.DataSourceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 邻里圈动态服务类
 */
public class CommunityPostService {
    
    private CommunityPostDao communityPostDao = new CommunityPostDao();
    private final Map<Integer, CommunityPost> inMemoryPosts = new HashMap<>();
    private final AtomicInteger postIdGenerator = new AtomicInteger(1);
    
    /**
     * 创建新的邻里圈动态
     * @param userId 用户ID
     * @param content 内容
     * @param images 图片URL（JSON格式）
     * @param visibility 可见性
     * @return 创建的邻里圈动态，如果失败返回null
     */
    public CommunityPost createPost(int userId, String content, String images, String visibility) {
        if (!isDatabaseAvailable()) {
            CommunityPost post = new CommunityPost();
            post.setId(postIdGenerator.getAndIncrement());
            post.setUserId(userId);
            post.setContent(content);
            post.setImages(images);
            post.setVisibility(visibility);
            inMemoryPosts.put(post.getId(), post);
            return post;
        }
        CommunityPost post = new CommunityPost();
        post.setUserId(userId);
        post.setContent(content);
        post.setImages(images);
        post.setVisibility(visibility);
        
        if (communityPostDao.createPost(post)) {
            return post;
        }
        return null;
    }
    
    /**
     * 根据ID获取邻里圈动态
     * @param id 动态ID
     * @return CommunityPost对象
     */
    public CommunityPost getPostById(int id) {
        if (!isDatabaseAvailable()) {
            return inMemoryPosts.get(id);
        }
        return communityPostDao.findById(id);
    }
    
    /**
     * 获取所有公开的邻里圈动态
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 邻里圈动态列表
     */
    public List<CommunityPost> getPublicPosts(int page, int size) {
        if (!isDatabaseAvailable()) {
            List<CommunityPost> publicPosts = new ArrayList<>();
            for (CommunityPost post : inMemoryPosts.values()) {
                if ("public".equals(post.getVisibility())) {
                    publicPosts.add(post);
                }
            }
            int fromIndex = Math.max(0, (page - 1) * size);
            int toIndex = Math.min(publicPosts.size(), fromIndex + size);
            if (fromIndex >= publicPosts.size()) {
                return new ArrayList<>();
            }
            return publicPosts.subList(fromIndex, toIndex);
        }
        int offset = (page - 1) * size;
        return communityPostDao.getPublicPosts(offset, size);
    }
    
    /**
     * 获取指定用户的所有邻里圈动态
     * @param userId 用户ID
     * @return 邻里圈动态列表
     */
    public List<CommunityPost> getPostsByUserId(int userId) {
        if (!isDatabaseAvailable()) {
            List<CommunityPost> result = new ArrayList<>();
            for (CommunityPost post : inMemoryPosts.values()) {
                if (post.getUserId() == userId) {
                    result.add(post);
                }
            }
            return result;
        }
        return communityPostDao.getPostsByUserId(userId);
    }
    
    /**
     * 更新邻里圈动态的点赞数
     * @param postId 动态ID
     * @param likeCount 点赞数
     * @return 是否更新成功
     */
    public boolean updateLikeCount(int postId, int likeCount) {
        if (!isDatabaseAvailable()) {
            CommunityPost post = inMemoryPosts.get(postId);
            if (post == null) {
                return false;
            }
            post.setLikeCount(likeCount);
            return true;
        }
        return communityPostDao.updateLikeCount(postId, likeCount);
    }
    
    /**
     * 更新邻里圈动态的评论数
     * @param postId 动态ID
     * @param commentCount 评论数
     * @return 是否更新成功
     */
    public boolean updateCommentCount(int postId, int commentCount) {
        if (!isDatabaseAvailable()) {
            CommunityPost post = inMemoryPosts.get(postId);
            if (post == null) {
                return false;
            }
            post.setCommentCount(commentCount);
            return true;
        }
        return communityPostDao.updateCommentCount(postId, commentCount);
    }
    
    /**
     * 删除邻里圈动态
     * @param postId 动态ID
     * @return 是否删除成功
     */
    public boolean deletePost(int postId) {
        if (!isDatabaseAvailable()) {
            return inMemoryPosts.remove(postId) != null;
        }
        return communityPostDao.deletePost(postId);
    }

    private boolean isDatabaseAvailable() {
        return DataSourceUtil.isInitialized();
    }
}
