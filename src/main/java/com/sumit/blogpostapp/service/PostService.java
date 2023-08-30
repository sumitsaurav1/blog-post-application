package com.sumit.blogpostapp.service;

import com.sumit.blogpostapp.model.Post;
import com.sumit.blogpostapp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService
{
    @Autowired
    private PostRepository postRepository;

    public Page<Post> findAllPostPaginated(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Optional<Post> getPostById(Integer id) {
        return postRepository.findById(id);
    }

    public Post savePost(Post post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }else {
            post.setUpdatedAt(LocalDateTime.now());
        }

        return postRepository.save(post);
    }

    public void deletePostById(Integer id) {
        postRepository.deleteById(id);
    }

    public List<Post> findAllDraftPosts() {
        return postRepository.findAllDraftPost();
    }

    public List<String> findAuthorsBySearch(List<Post> content) {
       return postRepository.findAuthorsBySearch(content);
    }

    public List<Post> findAllPostsWithoutPagination() {
        return postRepository.findAllWithoutPagination();
    }

    public List<Post> findMyAllPublishedPost(String userEmail) {
        return  postRepository.findMyAllPublishedPost(userEmail);
    }
}
