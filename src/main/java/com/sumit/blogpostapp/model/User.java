package com.sumit.blogpostapp.model;

import jakarta.persistence.*;
//import net.bytebuddy.dynamic.loading.InjectionClassLoader;

//import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;
    @Column(name = "role")
    private String role;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
