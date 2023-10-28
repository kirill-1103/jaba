package sovcombank.jabka.libs.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    STUDENT("ROLE_STUDENT"),

    ADMIN("ROLE_ADMIN"),

    TEACHER("ROLE_TEACHER"),

    CURATOR("ROLE_CURATOR"),

    ENROLLEE("ROLE_ENROLLEE"),

    MODERATOR("ROLE_MODERATOR"),

    COMMITTE("ROLE_COMMITTE");

    private String value;
}
