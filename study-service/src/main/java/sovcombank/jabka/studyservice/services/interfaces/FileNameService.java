package sovcombank.jabka.studyservice.services.interfaces;

import org.springframework.stereotype.Service;
import sovcombank.jabka.studyservice.models.FileName;

@Service
public interface FileNameService {
    FileName saveFileName(FileName fileName);
    String getNameByFileId(Long id);
}
