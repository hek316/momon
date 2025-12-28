package com.momon.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region:ap-northeast-2}")
    private String region;

    @Value("${ai.mock.enabled:true}")
    private boolean mockModeEnabled;

    public String uploadMonsterImage(String imageUrl) {
        // In mock mode, skip S3 upload and return the original URL
        if (mockModeEnabled) {
            log.info("🎭 Mock mode: Skipping S3 upload, returning original URL");
            log.info("   - URL: {}", imageUrl);
            return imageUrl;
        }

        try {
            // 1. Download image from URL
            byte[] imageBytes = downloadImage(imageUrl);
            log.info("📥 Downloaded image from URL: {} ({} bytes)", imageUrl, imageBytes.length);

            // 2. Generate unique filename
            String filename = "monsters/" + UUID.randomUUID() + ".png";

            // 3. Upload to S3
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType("image/png")
                .build();

            s3Client.putObject(request, RequestBody.fromBytes(imageBytes));

            // 4. Generate S3 URL
            String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, filename);

            log.info("✅ Image uploaded to S3: {}", s3Url);
            return s3Url;

        } catch (Exception e) {
            log.error("❌ S3 upload failed for URL: {}", imageUrl, e);
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    private byte[] downloadImage(String imageUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(imageUrl))
            .GET()
            .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to download image: HTTP " + response.statusCode());
        }

        return response.body();
    }
}
