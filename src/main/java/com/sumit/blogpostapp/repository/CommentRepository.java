package com.sumit.blogpostapp.repository;

import com.sumit.blogpostapp.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
