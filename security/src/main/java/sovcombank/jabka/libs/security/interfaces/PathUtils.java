package sovcombank.jabka.libs.security.interfaces;

import lombok.NonNull;

public interface PathUtils {
    @NonNull String[] getPublic();

    @NonNull String[] getForAuthorized();

    @NonNull String[] getForAdmin();

    @NonNull String[] getForStudent();

    @NonNull String[] getForTeacher();

    @NonNull String[] getForCurator();

    @NonNull String[] getForEnrollee();

    @NonNull String[] getForCommitte();

    @NonNull String[] getForModerator();
}
