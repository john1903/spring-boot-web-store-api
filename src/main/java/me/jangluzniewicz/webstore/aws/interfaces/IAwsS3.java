package me.jangluzniewicz.webstore.aws.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface IAwsS3 {
  String uploadFile(String key, MultipartFile file);
}
