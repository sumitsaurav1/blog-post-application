package com.sumit.blogpostapp.controller;

import com.sumit.blogpostapp.model.User;
import com.sumit.blogpostapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class ResisterController
{
    @Autowired
    private UserService userService;
    @GetMapping("/resister")
    public String getResisterPage(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "resister";
    }

    @PostMapping("/resister")
    public String resisterNewUser(@ModelAttribute User user,Model model){
        Optional<User> userOptional = userService.findByEmail(user.getEmail());
        if(userOptional.isPresent()){
            System.out.println(userService.findByEmail(user.getEmail()));
            model.addAttribute("error","This email is exits");
            model.addAttribute("user",user);
            return "resister";
        }
        user.setPassword("{noop}"+user.getPassword());
        user.setRole("ROLE_Author");
        userService.save(user);
        return "redirect:/";
    }
}
