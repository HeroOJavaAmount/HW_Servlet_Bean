package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            long newId = nextId.getAndIncrement();
            post.setId(newId);
            posts.put(newId, post);
            return post;
        } else {
            long id = post.getId();
            if (!posts.containsKey(id)) {
                throw new NotFoundException("Post with id " + id + " not found");
            }
            posts.put(id, post);
            return post;
        }
    }

    public void removeById(long id) {
        if (!posts.containsKey(id)) {
            throw new NotFoundException("Post with id " + id + " not found");
        }
        posts.remove(id);
    }
}
