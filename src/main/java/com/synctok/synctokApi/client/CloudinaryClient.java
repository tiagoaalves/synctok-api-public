package com.synctok.synctokApi.client;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Client for interacting with the Cloudinary service.
 * This class provides methods for uploading files to Cloudinary and retrieving their public URLs.
 */
@Component
@SuppressWarnings("unchecked")
public final class CloudinaryClient {

    private final Cloudinary cloudinary;

    /**
     * Constructs a new CloudinaryClient with the specified Cloudinary instance.
     *
     * @param cloudinary the Cloudinary instance to use for file operations
     */
    @Autowired
    public CloudinaryClient(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads a file to Cloudinary and retrieves its public URL.
     *
     * @param file the MultipartFile to upload
     * @return the public URL of the uploaded file
     * @throws IOException if there's an error during file upload
     */
    public String uploadAndGetPublicUrl(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "video"));
        return (String) uploadResult.get("url");
    }
}
