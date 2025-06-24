package com.example.moviefinder.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class ImageDownloader {

    public List<String> downloadImages(JSONObject imageData, String title) {
        JSONArray backdrops = imageData.optJSONArray("backdrops");
        if (backdrops == null || backdrops.isEmpty()) return List.of();

        String safeTitle = title.replaceAll("[\\/:*?\"<>|]", "_");
        Path folderPath = Paths.get(safeTitle);
        List<String> imagePaths = new ArrayList<>();

        try {
            Files.createDirectories(folderPath);

            IntStream.range(0, Math.min(3, backdrops.length()))
                    .mapToObj(i -> backdrops.getJSONObject(i).optString("file_path"))
                    .filter(path -> !path.isEmpty())
                    .forEach(filePath -> {
                        try {
                            Path outputPath = folderPath.resolve(filePath.replace("/", "_"));
                            URI uri = new URI("https", "image.tmdb.org", "/t/p/w780" + filePath, null);
                            downloadToFile(uri.toURL(), outputPath);
                            imagePaths.add(outputPath.toString());
                        } catch (Exception e) {
                            System.err.println("⚠ Failed to download image: " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            System.err.println("⚠ Failed to create image directory: " + e.getMessage());
        }

        return imagePaths;
    }

    private void downloadToFile(URL url, Path outputPath) throws IOException {
        try (InputStream in = new BufferedInputStream(url.openStream());
             OutputStream out = new FileOutputStream(outputPath.toFile())) {
            in.transferTo(out); // Java 9+ API simplifies the copy
        }
    }
}