package com.sumit.blogpostapp.repository;
import com.sumit.blogpostapp.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE p.isPublished = true")
    Page<Post> findAll(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true")
    List<Post> findAllWithoutPagination();
    @Query("SELECT p FROM Post p WHERE p.isPublished= false")
    List<Post> findAllDraftPost();

    @Query("SELECT DISTINCT p.author FROM Post p WHERE p.isPublished = true AND p IN :content")
    List<String> findAuthorsBySearch(@Param("content") List<Post> content);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN post.tags tag " +
            "WHERE (:queryValue = 1 OR " +
            "(post.title LIKE %:searchRequest% " +
            "OR post.content LIKE %:searchRequest% " +
            "OR post.author LIKE %:searchRequest% " +
            "OR tag.tagName LIKE %:searchRequest%)) " +
            "AND (:emptyAuthors = 1 OR post.author IN :authors) " +
            "AND (:emptyTags = 1 OR tag.tagName IN :tagNames) " +
            "AND (:dates = 1 OR FUNCTION('DATE', post.publishedAt) BETWEEN :startDate AND :endDate) " +
            "AND post.isPublished = true")
    Page<Post> filterAndSearchPosts(
            @Param("searchRequest") String searchRequest,
            @Param("emptyAuthors") int emptyAuthors,
            @Param("emptyTags") int emptyTags,
            @Param("dates") int dates,
            @Param("authors") List<String> authors,
            @Param("tagNames") List<String> tagNames,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("queryValue") int queryValue,
            Pageable pageable);
    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN post.tags tag " +
            "WHERE (:queryValue = 1 OR " +
            "(post.title LIKE %:searchRequest% " +
            "OR post.content LIKE %:searchRequest% " +
            "OR post.author LIKE %:searchRequest% " +
            "OR tag.tagName LIKE %:searchRequest%)) " +
            "AND (:emptyAuthors = 1 OR post.author IN :authors) " +
            "AND (:emptyTags = 1 OR tag.tagName IN :tagNames) " +
            "AND (:dates = 1 OR FUNCTION('DATE', post.publishedAt) BETWEEN :startDate AND :endDate) " +
            "AND post.isPublished = true")
    List<Post> filterAndSearchPostsWithoutPagination(
            @Param("searchRequest") String searchRequest,
            @Param("emptyAuthors") int emptyAuthors,
            @Param("emptyTags") int emptyTags,
            @Param("dates") int dates,
            @Param("authors") List<String> authors,
            @Param("tagNames") List<String> tagNames,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("queryValue") int queryValue);

    @Query("SELECT DISTINCT post FROM Post post LEFT JOIN post.user user " +
            "WHERE (user.email = :userEmail or post.adminEmail =:userEmail) AND post.isPublished = true")
    List<Post> findMyAllPublishedPost(@Param("userEmail") String userEmail);
//    @Query("SELECT DISTINCT post FROM Post post LEFT JOIN post.user user " +
//            "WHERE user.email = :userEmail AND post.isPublished = true " +
//            "AND (user.role = 'ROLE_Admin' AND post.adminEmail IS NOT NULL) " +
//            "OR (user.role != 'ROLE_Admin' AND post.adminEmail IS NULL)")
//    List<Post> findMyAllPublishedPosts(@Param("userEmail") String userEmail);

}
