package me.jangluzniewicz.webstore.aws.services;

import java.util.UUID;
import me.jangluzniewicz.webstore.aws.interfaces.IAwsS3;
import me.jangluzniewicz.webstore.exceptions.AwsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service implements IAwsS3 {
  private final S3Client s3Client;
  private final String bucketName;
  private final String region;

  public S3Service(
      S3Client s3Client,
      @Value("${aws.s3.bucket}") String bucketName,
      @Value("${aws.s3.region}") String region) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
    this.region = region;
  }

  @Override
  public String uploadFile(String key, MultipartFile file) {
    key = key + UUID.randomUUID() + "_" + file.getOriginalFilename();
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(key).build();
    try {
      s3Client.putObject(
          putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
      return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    } catch (Exception e) {
      throw new AwsException("Error uploading file to S3");
    }
  }
}
