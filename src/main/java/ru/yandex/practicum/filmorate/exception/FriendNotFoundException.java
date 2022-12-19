package ru.yandex.practicum.filmorate.exception;

public class FriendNotFoundException extends RuntimeException {
    private final Integer userId;
    private final Integer friendId;

    public FriendNotFoundException(Integer userId, Integer friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getFriendId() {
        return friendId;
    }
}