package com.sumit.blogpostapp.controller;

import com.sumit.blogpostapp.model.Post;
import com.sumit.blogpostapp.model.Tag;
import com.sumit.blogpostapp.model.User;
import com.sumit.blogpostapp.service.PostService;
import com.sumit.blogpostapp.service.TagService;
import com.sumit.blogpostapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class PostController
{
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Integer id, Model model){
        Optional<Post> optionalPost = postService.getPostById(id);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            model.addAttribute("post",post);
            return "post";
        }
        return "404";
    }

    @GetMapping("/posts/new")
    public String createNewPost(Model model){
//        Optional<User> userOptional = userService.findByEmail("user@gmail.com");
//        if(userOptional.isPresent()){
//            Post post = new Post();
//            User user = userOptional.get();
//            post.setUser(user);
////            post.setAuthor(user.getName());
//            model.addAttribute("post",post);
//            return "post_new";
//        }else{
//            return "404";
//        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(userEmail);
        User user = userOptional.get();
        Post post = new Post();
        model.addAttribute("user",user);
        model.addAttribute("post",post);
        return "post_new";
    }

    @PostMapping("/posts/new")
    public String saveNewPost(@ModelAttribute Post post , @RequestParam("tagsInput") String tagsInput,
                              @RequestParam("action") String action,
                              @RequestParam(value = "enteredEmail",required = false) String enteredEmail
                              ){
        if ("draft".equals(action)) {
            post.setIsPublished(false);
            System.out.println("draft");
        } else if ("publish".equals(action)) {
            post.setIsPublished(true);
            post.setPublishedAt(LocalDateTime.now());
            System.out.println("publish");
        }

        String[] tagNames = tagsInput.split(",");
        Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tagNames) {
            tagName = tagName.trim();
            if(!tagName.isEmpty()){
                Tag tag = tagService.createOrGetTagByName(tagName);
                tagSet.add(tag);
            }
        }
        List<Tag> tags = new ArrayList<>(tagSet);
        post.setTags(tags);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userEmail = authentication.getName();
//        Optional<User> userOptional = userService.findByEmail(userEmail);
//        User user = userOptional.get();
//        post.setUser(user);
//        post.setAuthor(user.getName());



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        String roleName="";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
             roleName = authority.getAuthority();
            System.out.println("Role: " + roleName);
        }

        if(roleName.equals("ROLE_Admin") && enteredEmail!=null){
            Optional<User> userOptional = userService.findByEmail(enteredEmail);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                post.setUser(user);
                post.setAuthor(user.getName());
                post.setAdminEmail(userEmail);
            }else{
                return "post_new";
            }
        }else{
            Optional<User> userOptional = userService.findByEmail(userEmail);
            User user = userOptional.get();
            post.setUser(user);
            post.setAuthor(user.getName());
        }

        postService.savePost(post);
        return "redirect:/posts/"+post.getId();
    }

    @GetMapping("/posts/{id}/edit")
    public String getPostForUpdate(@PathVariable Integer id, Model model){
        Optional<Post> optionalPost = postService.getPostById(id);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            List<Tag> tags = post.getTags();
            String tagString="";

            for(int i=0;i<tags.size();i++){
                tagString += tags.get(i).getTagName();
                if(i != tags.size()-1){
                    tagString += ", ";
                }
            }

            model.addAttribute("tagString", tagString);
            model.addAttribute("post",post);
            return "post_update";
        }else{
            return "404";
        }
    }

    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable Integer id, Post post, Model model, @RequestParam("tagsInput") String tagsInput ){
        Optional<Post> optionalPost = postService.getPostById(id);
        if(optionalPost.isPresent()){
            Post existingPost = optionalPost.get();
            existingPost.setTitle(post.getTitle());
            existingPost.setExcrept(post.getExcrept());
            existingPost.setContent(post.getContent());
            existingPost.setUser(post.getUser());
            String[] tagNames = tagsInput.split(",");
            Set<Tag> tagSet = new HashSet<>();

            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if(!tagName.isEmpty()){
                    Tag tag = tagService.createOrGetTagByName(tagName);
                    tagSet.add(tag);
                }
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> userOptional = userService.findByEmail(userEmail);
            User user = userOptional.get();
            existingPost.setUpdatedBy(user.getName());

            List<Tag> tags = new ArrayList<>(tagSet);
            existingPost.setTags(tags);
            postService.savePost(existingPost);
        }
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Integer id, Model model){
        postService.deletePostById(id);
        return "redirect:/";
    }

    @GetMapping("/see/drafts")
    String showDrafts(Model model){
        List<Post> draftPost = postService.findAllDraftPosts();
        model.addAttribute("posts",draftPost);
        return "draft_post";
    }
    @GetMapping("/posts/{id}/publish")
    public String publishPost(@PathVariable Integer id, Model model){
        Optional<Post> optionalPost = postService.getPostById(id);

        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            post.setIsPublished(true);
            post.setPublishedAt(LocalDateTime.now());
            postService.savePost(post);
            return "redirect:/posts/" + post.getId();
        }
        return "404";
    }

    @GetMapping("/myPost")
    public String myPosts(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(userEmail);
        List<Post> myPublishedPost = postService.findMyAllPublishedPost(userEmail);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            model.addAttribute("user",user);
            model.addAttribute("myPublishedPost",myPublishedPost);
            return "my_posts";
        }else{
            return "404";
        }
    }
}
