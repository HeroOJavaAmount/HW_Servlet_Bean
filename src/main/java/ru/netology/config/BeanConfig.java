package ru.netology.config;

import org.springframework.context.annotation.Bean;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;


public class BeanConfig {

    @Bean
    public PostRepository postRepository(){
    return new PostRepository();
    }

    @Bean
    public PostService postService(){
        return new PostService(postRepository());
    }

    @Bean
    public PostController postController() {
        return new PostController(postService());
    }
}
