package sovcombank.jabka.studyservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {
    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.baseUri}")
    private String baseUri;

    @Bean
    public S3Client s3Client(){

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId,secretKey );
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        return S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .endpointOverride(URI.create(baseUri))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
