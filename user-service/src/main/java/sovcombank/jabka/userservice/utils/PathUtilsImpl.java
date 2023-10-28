package sovcombank.jabka.userservice.utils;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import sovcombank.jabka.libs.security.interfaces.PathUtils;

@Component
public class PathUtilsImpl implements PathUtils {

    private final static String[] PUBLIC_PATHS = new String[]{
            "/api/user/auth/signin",
            "/api/user/auth/signup",
            "/api/user/auth/activation",
            "/api/user/auth/forget",
            "/api/user/auth/recovery"
    };

    private final static String[] AUTHORIZED_PATHS = new String[]{
            "/api/user/**"
    };

    @Override
    public @NonNull String[] getPublic() {
        return PUBLIC_PATHS;
    }

    @Override
    public @NonNull String[] getForAuthorized() {
        return AUTHORIZED_PATHS;
    }

    @Override
    public @NonNull String[] getForAdmin() {
        return new String[0];
    }

    @Override
    public @NonNull String[] getForStudent() {
        return new String[0];
    }

    @Override
    public @NonNull String[] getForTeacher() {
        return new String[0];
    }

    @Override
    public @NonNull String[] getForCurator() {
        return new String[0];
    }

    @Override
    public @NonNull String[] getForEnrollee() {
        return new String[0];
    }

    @Override
    public @NonNull String[] getForCommitte() {
        return new String[0];
    }

    @Override
    public @NonNull String[] getForModerator() {
        return new String[0];
    }
}
