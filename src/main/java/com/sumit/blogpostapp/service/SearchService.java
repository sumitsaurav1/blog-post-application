package com.sumit.blogpostapp.service;

import com.sumit.blogpostapp.model.Post;
import com.sumit.blogpostapp.repository.PostRepository;
//import com.sumit.blogpostapp.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchService
{
    @Autowired
    private PostRepository postRepository;
    public Page<Post> findAllSearchedPost(Pageable pageable, String query, List<String> slectedAuthors, List<String> slectedTags,
                             LocalDate startDate, LocalDate endDate) {
        int queryValue = query==null ? 1:0;
        int authorList = slectedAuthors == null ? 1:0;
        int tagList = slectedTags == null ? 1:0;
        int startAndEndDate = (startDate == null || endDate == null) ? 1:0;
        return postRepository.filterAndSearchPosts(query, authorList, tagList, startAndEndDate,
                slectedAuthors, slectedTags, startDate, endDate,queryValue,pageable);
    }

    public List<Post> findAllSearchedWithoutPagination(String query, List<String>slectedAuthors, List<String> slectedTags, LocalDate startDate, LocalDate endDate) {
        int queryValue = query==null ? 1:0;
        int authorList = slectedAuthors == null ? 1:0;
        int tagList = slectedTags == null ? 1:0;
        int startAndEndDate = (startDate == null || endDate == null) ? 1:0;
        return postRepository.filterAndSearchPostsWithoutPagination(query, authorList, tagList, startAndEndDate,
                slectedAuthors, slectedTags, startDate, endDate,queryValue);
    }
}
