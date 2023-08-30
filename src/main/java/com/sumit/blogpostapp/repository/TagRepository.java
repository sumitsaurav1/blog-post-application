package com.sumit.blogpostapp.repository;

import com.sumit.blogpostapp.model.Post;
import com.sumit.blogpostapp.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByTagName(String tagName);
    @Query("SELECT DISTINCT t.tagName FROM Tag t " +
            "JOIN t.posts p " +
            "WHERE p.isPublished = true")
    List<String> findTagsOfPublishedPosts();

    @Query("SELECT p FROM Post p LEFT JOIN p.tags t WHERE t.tagName = :tag")
    List<Post> findPostByTag(@Param("tag") String tag);

    @Query("SELECT DISTINCT t.tagName FROM Tag t LEFT JOIN t.posts p WHERE p.isPublished = true AND p IN :content")
    List<String> findTagsBySearch(@Param("content") List<Post> content);

}
