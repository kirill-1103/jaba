package sovcombank.jabka.studyservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import sovcombank.jabka.studyservice.exceptions.FileException;
import sovcombank.jabka.studyservice.exceptions.NotFoundException;
import sovcombank.jabka.studyservice.models.enums.StudyMaterialsType;
import sovcombank.jabka.studyservice.services.interfaces.FileService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${spring.application.name}")
    private String baseFolder;

    @Value("${aws.bucketName}")
    private String bucketName;

    private final S3Client s3Client;

    @Override
    public ResponseEntity<ByteArrayResource> getFileByPath(String path) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest
                    .builder()
                    .key(String.format("%s/%s", baseFolder, path))
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(getObjectRequest);
            ByteArrayResource byteArrayResource = new ByteArrayResource(response.asByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                    "filename=" + path.substring(path.lastIndexOf('/') + 1));
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(response.asByteArray().length));

            return ResponseEntity.ok().headers(headers).body(byteArrayResource);
        } catch (NoSuchKeyException e) {
            throw new NotFoundException("File not found.");
        } catch (NoSuchBucketException e) {
            throw new NotFoundException("Folder not found.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileException("Failed get file. Error:" + e.getMessage());
        }
    }

    @Override
    public void save(String path, MultipartFile multipartFile) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .key(String.format("%s/%s", baseFolder, path))
                    .bucket(bucketName)
                    .build();
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(multipartFile.getBytes()));
        } catch (NoSuchBucketException e) {
            throw new NotFoundException("Folder not found.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileException("Failed load file. Error:" + e.getMessage());
        }
    }


    @Override
    public void save(String path, Resource resource) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder()
                    .key(String.format("%s/%s", baseFolder, path))
                    .bucket(bucketName)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(resource.getContentAsByteArray()));
        } catch (NoSuchBucketException e) {
            throw new NotFoundException("Folder not found.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileException("Failed load file. Error:" + e.getMessage());
        }
    }
    @Override
    public void removeFileByPath(String path) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(String.format("%s/%s", baseFolder, path))
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e){
            throw new FileException("Failed remove file.");
        }
    }

    @Override
    public String generateMaterialsFileName(String fileName, StudyMaterialsType materialsType) {
        String formattedDate = getDateString();
        String newFileName = "_"+formattedDate+"_"+UUID.randomUUID()+"_"+fileName;
        switch (materialsType) {
            case TASK -> {
                return "TASK"+newFileName;
            }
            case MATERIAL ->  {
                return "MATERIAL"+newFileName;
            }
        }
        return null;
    }

    @Override
    public String generateHomeworkFileName(String fileName, Long studentId){
        String formattedDate = getDateString();
        return "HOMEWORK_"+studentId+"_"+formattedDate+"_"+UUID.randomUUID()+"_"+fileName;
    }

    private String getDateString(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }
}
