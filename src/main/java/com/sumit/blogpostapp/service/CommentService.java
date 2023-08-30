package com.sumit.blogpostapp.service;

import com.sumit.blogpostapp.model.Comment;
import com.sumit.blogpostapp.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService
{
    @Autowired
    private CommentRepository commentRepository;

    public Comment saveComment(Comment comment) {
        if (comment.getId() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
        else{
            comment.setUpdatedAt(LocalDateTime.now());
        }
       return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Integer commentId) {
        return commentRepository.findById(commentId);

    }

    public void deleteCommentById(Integer commentId) {
        commentRepository.deleteById(commentId);
    }
}
