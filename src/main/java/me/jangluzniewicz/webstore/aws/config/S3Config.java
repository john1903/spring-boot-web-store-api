package me.jangluzniewicz.webstore.aws.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {
  private final String accessKey;
  private final String secretKey;
  private final String url;
  private final Region region;

  public S3Config(
      @Value("${aws.access-key}") String accessKey,
      @Value("${aws.secret-key}") String secretKey,
      @Value("${aws.url}") String url,
      @Value("${aws.region}") String region) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.url = url;
    this.region = Region.of(region);
  }

  @Bean
  public S3Client s3Client() {
    AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
    return S3Client.builder()
        .endpointOverride(URI.create(url))
        .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
        .region(region)
        .build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
    return S3Presigner.builder()
        .endpointOverride(URI.create(url))
        .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
        .region(region)
        .build();
  }
}
