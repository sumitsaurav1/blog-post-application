package com.sumit.blogpostapp.service;

import com.sumit.blogpostapp.model.Post;
import com.sumit.blogpostapp.model.Tag;
import com.sumit.blogpostapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TagService
{
    @Autowired
    private TagRepository tagRepository;

    public Tag createOrGetTagByName(String tagName) {
        Optional<Tag> tagOptional = tagRepository.findByTagName(tagName);
        if (tagOptional.isPresent() ) {
            return tagOptional.get();
        }

        Tag newTag = new Tag();
        newTag.setTagName(tagName);
        return tagRepository.save(newTag);
    }

    public List<String> findTagsBySearch(List<Post> content) {
        return tagRepository.findTagsBySearch(content);
    }
}
