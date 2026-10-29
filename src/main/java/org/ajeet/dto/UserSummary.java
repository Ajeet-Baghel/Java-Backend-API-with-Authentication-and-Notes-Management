package org.ajeet.dto;

import org.ajeet.model.Role;
import org.ajeet.model.User;

public record UserSummary(Long id, String username, String email, Role role) {

    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
