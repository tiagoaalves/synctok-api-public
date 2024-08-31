package com.synctok.synctokApi.client;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@SuppressWarnings("unchecked")
public class CloudinaryClient {

    private final Cloudinary cloudinary;

    @Autowired
    private CloudinaryClient(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadAndGetPublicUrl(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "video"));
        return (String) uploadResult.get("url");
    }

}
