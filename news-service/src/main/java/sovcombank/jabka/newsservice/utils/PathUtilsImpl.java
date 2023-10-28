package sovcombank.jabka.newsservice.utils;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import sovcombank.jabka.libs.security.interfaces.PathUtils;

@Component
public class PathUtilsImpl implements PathUtils {

    private final static String[] PUBLIC_PATHS = new String[]{
            "/**"
    };

    private final static String[] AUTHORIZED_PATHS = new String[]{
            "/api/news/**"
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
        return AUTHORIZED_PATHS;
    }

    @Override
    public @NonNull String[] getForStudent() {
        return AUTHORIZED_PATHS;
    }

    @Override
    public @NonNull String[] getForTeacher() {
        return AUTHORIZED_PATHS;
    }

    @Override
    public @NonNull String[] getForCurator() {
        return AUTHORIZED_PATHS;
    }

    @Override
    public @NonNull String[] getForEnrollee() {
        return AUTHORIZED_PATHS;
    }

    @Override
    public @NonNull String[] getForCommitte() {
        return AUTHORIZED_PATHS;
    }

    @Override
    public @NonNull String[] getForModerator() {
        return AUTHORIZED_PATHS;
    }
}
