package com.sumit.blogpostapp.controller;

import com.sumit.blogpostapp.model.Post;
import com.sumit.blogpostapp.model.User;
import com.sumit.blogpostapp.service.PostService;
import com.sumit.blogpostapp.service.SearchService;
import com.sumit.blogpostapp.service.TagService;
import com.sumit.blogpostapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController
{
    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SearchService searchService;
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public  String home( @RequestParam(value = "pageNo",required = false) Integer pageNo,
                        Model model){
        int page = (pageNo != null && pageNo > 0) ? pageNo : 1;
        int pageSize =5;
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("publishedAt").descending());
        Page<Post> postPage = postService.findAllPostPaginated(pageable);

        List<Post> allPost = postService.findAllPostsWithoutPagination();
        List<String> allAuthors = postService.findAuthorsBySearch(allPost);
        List<String> allTags = tagService.findTagsBySearch(allPost);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(userEmail);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            model.addAttribute("userName",user.getName());
        }

        StringBuilder paginationURL = new StringBuilder("/?").append("pageSize=").append(pageSize);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("paginationURL",paginationURL);
        model.addAttribute("authors",allAuthors);
        model.addAttribute("tags",allTags);


        return "home";
    }

    @GetMapping("/search")
    public String searchPost(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "pageNo",required = false) Integer pageNo,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
            @RequestParam(value = "selectedAuthors", required = false) List<String> selectedAuthors,
            @RequestParam(value = "selectedTags", required = false) List<String> selectedTags,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "home" ,required = false) String home,
            @RequestParam(value = "userName",required = false) String userName,
            Model model
    ) {
        int page = (pageNo != null && pageNo > 0) ? pageNo : 1;
        int pageSize =5;
        Pageable pageable;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            pageable = PageRequest.of(page-1, pageSize, Sort.by("publishedAt").ascending());
        } else {
            pageable = PageRequest.of(page-1, pageSize, Sort.by("publishedAt").descending());
        }

        if(home!= null){
            selectedAuthors = null;
            selectedTags = null;
            query= null;
            startDate = null;
            endDate = null;
        }

        System.out.println(selectedAuthors);
        System.out.println(selectedTags);
        System.out.println(startDate);
        System.out.println(endDate);
        System.out.println(query);

        List<String> authorsBySearch = new ArrayList<>();
        List<String> tagsBySearch = new ArrayList<>();

        boolean isHome = true;
        Page<Post> postPage;
        System.out.println("search");
        postPage = searchService.findAllSearchedPost(pageable, query,selectedAuthors,selectedTags,startDate,endDate);
        List<Post> allPost =searchService.findAllSearchedWithoutPagination(query,selectedAuthors,selectedTags,startDate,endDate);
        authorsBySearch = postService.findAuthorsBySearch(allPost);
        tagsBySearch  =  tagService.findTagsBySearch(allPost);

        StringBuilder paginationURL = new StringBuilder("/search?").append("pageSize=").append(pageSize);
        if(query!= null){
            paginationURL.append("&query=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
        }
        if(sortOrder!= null){
            paginationURL.append("&sortOrder=").append(sortOrder);
        }
        if(selectedAuthors!= null){
            for(String author : selectedAuthors){
                paginationURL.append("&selectedAuthors=").append(author);
            }
        }
        if(selectedTags!= null){
            for(String tag : selectedTags){
                paginationURL.append("&selectedTags=").append(tag);
            }

        }
        if (startDate!= null){
            paginationURL.append("&startDate=").append(startDate.toString());
        }
        if(endDate!= null){
            paginationURL.append("&endDate=").append(endDate.toString());
        }
        if(userName!=null){
            paginationURL.append("&userName=").append(userName);
        }

        model.addAttribute("paginationURL",paginationURL);
        model.addAttribute("authors",authorsBySearch);
        model.addAttribute("tags", tagsBySearch);
        model.addAttribute("selectedAuthors",selectedAuthors);
        model.addAttribute("selectedTags",selectedTags);
        model.addAttribute("query",query);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sortOrder", sortOrder);
        System.out.println(postPage.getTotalElements());
        model.addAttribute("isHome",isHome);
        model.addAttribute("userName",userName);
        return "home";
    }

    @GetMapping("/filter")
    public String filterPost(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "pageNo",required = false) Integer pageNo,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
            @RequestParam(value = "selectedAuthors", required = false) List<String> selectedAuthors,
            @RequestParam(value = "selectedTags", required = false) List<String> selectedTags,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "home" ,required = false) String home,
            @RequestParam(value = "authors",required = false) List<String> authors,
            @RequestParam(value = "tags",required = false) List<String> tags,
            @RequestParam(value = "userName",required = false) String userName,
            Model model
    ) {
        int page = (pageNo != null && pageNo > 0) ? pageNo : 1;
        int pageSize =5;
        Pageable pageable;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            pageable = PageRequest.of(page-1, pageSize, Sort.by("publishedAt").ascending());
        } else {
            pageable = PageRequest.of(page-1, pageSize, Sort.by("publishedAt").descending());
        }

        if(home!= null){
            selectedAuthors = null;
            selectedTags = null;
            query= null;
            startDate = null;
            endDate = null;
        }

        boolean isHome;
        Page<Post> postPage;
        if(selectedAuthors == null && selectedTags == null && query == null && startDate == null
                && endDate == null){
            postPage = postService.findAllPostPaginated(pageable);
            isHome = true;
        }else{
            System.out.println("this");
            postPage = searchService.findAllSearchedPost(pageable, query,selectedAuthors,selectedTags,startDate,endDate);
            isHome = true;
        }

        StringBuilder paginationURL = new StringBuilder("/filter?").append("pageSize=").append(pageSize);
        if(query!= null){
            paginationURL.append("&query=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
        }
        if(sortOrder!= null){
            paginationURL.append("&sortOrder=").append(sortOrder);
        }
        if(selectedAuthors!= null){

            for(String author : selectedAuthors){
                paginationURL.append("&selectedAuthors=").append(author);
            }
        }
        if(selectedTags!= null){
            for(String tag : selectedTags){
                paginationURL.append("&selectedTags=").append(tag);
            }
        }
        if (startDate!= null){
            paginationURL.append("&startDate=").append(startDate.toString());
        }
        if(endDate!= null){
            paginationURL.append("&endDate=").append(endDate.toString());
        }
        if(tags!=null){
            for(String tag : tags){
                paginationURL.append("&tags=").append(tag);
            }
        }
        if(authors!=null){
            for(String authore : authors){
                paginationURL.append("&authors=").append(authore);
            }
        }
        if(userName!=null){
            paginationURL.append("&userName=").append(userName);
        }

        model.addAttribute("paginationURL",paginationURL);
        model.addAttribute("selectedAuthors",selectedAuthors);
        model.addAttribute("selectedTags",selectedTags);
        model.addAttribute("query",query);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("authors",authors);
        model.addAttribute("tags", tags);
        model.addAttribute("isHome",isHome);
        model.addAttribute("userName",userName);

        System.out.println(authors);
        System.out.println(selectedAuthors);
        System.out.println(tags);
        System.out.println(selectedTags);
        System.out.println(query);
        System.out.println(postPage.getTotalElements());
        return "home";
    }
}
