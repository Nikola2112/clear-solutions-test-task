package com.ua.clear.repository;



import com.ua.clear.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
//In-Memory
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public User save(User user) {
        long id = idGenerator.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public void deleteById(Long id) {
        users.remove(id);
    }
}
