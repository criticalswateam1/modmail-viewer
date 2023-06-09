package com.github.khakers.modmailviewer.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserToken {
    long id;
    String username;
    String discriminator;
    String avatar;

    long[] roles = new long[]{};

    @JsonIgnore
    boolean isRealUser = true;

//    Role role;

    /**
     * Generates a fake SiteUSer with isRealUser set to false.
     */
//    public UserToken() {
//        this(0L, "anonymous", "0000", "",new long[]{});
//        this.isRealUser = false;
//    }
    public UserToken() {

    }
    public UserToken(long id, String username, String discriminator, String avatar, long[] roles, boolean isRealUser) {
        this.id = id;
        this.username = username;
        this.discriminator = discriminator;
        this.avatar = avatar;
        this.roles = roles;
        this.isRealUser = isRealUser;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public Optional<String> getAvatar() {
        return Optional.ofNullable(avatar);
    }

    public long[] getRoles() {
        return roles;
    }

    public boolean isRealUser() {
        return isRealUser;
    }

    public String getAvatarUrl() {
        if (avatar != null) {
            return String.format("https://cdn.discordapp.com/avatars/%s/%s.png", id, avatar);
        } else {
            return String.format("https://cdn.discordapp.com/embed/avatars/%d.png", Integer.parseInt(this.discriminator) % 5);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToken userToken = (UserToken) o;
        return id == userToken.id && isRealUser == userToken.isRealUser && Objects.equals(username, userToken.username) && Objects.equals(discriminator, userToken.discriminator) && Objects.equals(avatar, userToken.avatar) && Arrays.equals(roles, userToken.roles);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, username, discriminator, avatar, isRealUser);
        result = 31 * result + Arrays.hashCode(roles);
        return result;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", discriminator='" + discriminator + '\'' +
                ", avatar='" + avatar + '\'' +
                ", roles=" + Arrays.toString(roles) +
                ", isRealUser=" + isRealUser +
                '}';
    }
}
