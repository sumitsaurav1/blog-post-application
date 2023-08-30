package com.sumit.blogpostapp.controller;

import com.sumit.blogpostapp.model.Comment;
import com.sumit.blogpostapp.service.CommentService;
import com.sumit.blogpostapp.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sumit.blogpostapp.model.Post;
import java.util.Optional;
import org.springframework.ui.Model;

@Controller
public class CommentController
{
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;

    @PostMapping("/addComment/{postId}")
    public String addComment(
            @PathVariable Integer postId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String commentText
    ){
        Optional<Post> postOptional = postService.getPostById(postId);
        if(postOptional.isPresent()){
            Post post = postOptional.get();
            Comment comment = new Comment();
            comment.setName(name);
            comment.setEmail(email);
            comment.setCommentText(commentText);
            comment.setPost(post);

            commentService.saveComment(comment);
            return "redirect:/posts/" + post.getId();
        }
        return "404";
    }

    @GetMapping("/editComment/{commentId}")
    public String editComment(@PathVariable Integer commentId, Model model) {
        Optional<Comment> commentOptional = commentService.getCommentById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            model.addAttribute("comment", comment);
            return "comment_edit";
        }
        return "404";
    }

    @PostMapping("/editComment/{commentId}")
    public String saveEditedComment(
            @PathVariable Integer commentId,
            Comment comment, Model model
    ) {
        Optional<Comment> commentOptional = commentService.getCommentById(commentId);
        if (commentOptional.isPresent()) {
            Comment editedComment = commentOptional.get();
            editedComment.setCommentText(comment.getCommentText());
            commentService.saveComment(editedComment);
            return "redirect:/posts/" + editedComment.getPost().getId();
        }
        return "404";
    }

    @GetMapping("/cancelEdit/{commentId}")
    public String cancelEdit(@PathVariable Integer commentId) {
        Optional<Comment> commentOptional = commentService.getCommentById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            return "redirect:/posts/" + comment.getPost().getId();
        }
        return "404";
    }

    @GetMapping("/deleteComment/{commentId}")
    public String deleteComment(@PathVariable Integer commentId, Model model){
        Optional<Comment> commentOptional = commentService.getCommentById(commentId);
        if(commentOptional.isPresent()){
            Comment comment = commentOptional.get();
            Post post = comment.getPost();
            commentService.deleteCommentById(commentId);
            return "redirect:/posts/" + post.getId();
        }
        return "404";
    }
}
