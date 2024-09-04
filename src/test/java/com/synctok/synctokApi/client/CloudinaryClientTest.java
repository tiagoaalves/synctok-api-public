package com.synctok.synctokApi.client;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CloudinaryClientTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile multipartFile;

    private CloudinaryClient cloudinaryClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
        cloudinaryClient = new CloudinaryClient(cloudinary);
    }

    @Test
    void uploadAndGetPublicUrl_SuccessfulUpload() throws IOException {
        byte[] fileContent = "test content".getBytes();
        when(multipartFile.getBytes()).thenReturn(fileContent);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "https://res.cloudinary.com/demo/video/upload/v1573064751/sample-video.mp4");
        when(uploader.upload(eq(fileContent), any(Map.class))).thenReturn(uploadResult);

        String result = cloudinaryClient.uploadAndGetPublicUrl(multipartFile);

        assertEquals("https://res.cloudinary.com/demo/video/upload/v1573064751/sample-video.mp4", result);
        verify(uploader).upload(eq(fileContent), argThat(map ->
                map.containsKey("resource_type") && map.get("resource_type").equals("video")
        ));
    }

    @Test
    void uploadAndGetPublicUrl_UploadFails() throws IOException {
        byte[] fileContent = "test content".getBytes();
        when(multipartFile.getBytes()).thenReturn(fileContent);
        when(uploader.upload(eq(fileContent), any(Map.class))).thenThrow(new IOException("Upload failed"));

        assertThrows(IOException.class, () -> cloudinaryClient.uploadAndGetPublicUrl(multipartFile));
    }

    @Test
    void uploadAndGetPublicUrl_NullUrlInResponse() throws IOException {
        byte[] fileContent = "test content".getBytes();
        when(multipartFile.getBytes()).thenReturn(fileContent);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", null);
        when(uploader.upload(eq(fileContent), any(Map.class))).thenReturn(uploadResult);

        String result = cloudinaryClient.uploadAndGetPublicUrl(multipartFile);

        assertNull(result);
    }

    @Test
    void uploadAndGetPublicUrl_FileReadFails() throws IOException {
        when(multipartFile.getBytes()).thenThrow(new IOException("File read failed"));

        assertThrows(IOException.class, () -> cloudinaryClient.uploadAndGetPublicUrl(multipartFile));
    }
}
