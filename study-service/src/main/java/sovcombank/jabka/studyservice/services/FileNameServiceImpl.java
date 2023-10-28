package sovcombank.jabka.studyservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sovcombank.jabka.studyservice.exceptions.BadRequestException;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.models.FileName;
import sovcombank.jabka.studyservice.repositories.FileNameRepository;
import sovcombank.jabka.studyservice.services.interfaces.FileNameService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileNameServiceImpl implements FileNameService {
    private final FileNameRepository fileNameRepository;

    @Override
    public FileName saveFileName(FileName fileName) {
        if (fileName.getInitialName() == null || fileName.getNameS3() == null
                || fileName.getInitialName().isBlank() || fileName.getNameS3().isBlank()) {
            throw new BadRequestException("file name is blank");
        }
        return fileNameRepository.save(fileName);
    }

    @Override
    public String getNameByFileId(Long id) {
        Optional<FileName> fileNameOpt =fileNameRepository.findById(id);
        if(fileNameOpt.isEmpty()) {
            throw new NotFoundException(String.format("File with id %d wasn't found", id));
        }
        FileName fileName = fileNameOpt.get();
        return fileName.getNameS3();
    }
}
