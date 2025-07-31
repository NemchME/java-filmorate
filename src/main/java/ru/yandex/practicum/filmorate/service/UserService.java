package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        User updatedUser = userStorage.updateUser(user);
        if (updatedUser == null) {
            throw new ResourceNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return updatedUser;
    }

    public User getUser(int id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new ResourceNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int userId) {
        User user = getUser(userId);
        return user.getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = getUser(userId);
        User otherUser = getUser(otherUserId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::getUser)
                .collect(Collectors.toList());
    }
}