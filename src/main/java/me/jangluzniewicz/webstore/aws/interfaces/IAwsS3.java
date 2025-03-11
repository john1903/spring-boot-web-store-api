package me.jangluzniewicz.webstore.aws.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface IAwsS3 {
  String uploadFile(String folderPath, MultipartFile file);

  String getSignedUrl(String key);

  String updateFile(String key, MultipartFile file);

  void deleteFile(String key);
}
