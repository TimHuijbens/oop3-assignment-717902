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

/**
 * Utility class responsible for downloading images from TMDb using the
 * image file paths provided in a JSON response.
 */
@Component
public class ImageDownloader {

    /**
     * Downloads up to three images from the provided TMDb image data and stores them
     * in a local folder named after the movie title.
     *
     * @param imageData JSON object containing an array of "backdrops" from TMDb
     * @param title     the title of the movie (used to name the folder)
     * @return a list of relative paths where the images were saved
     */
    public List<String> downloadImages(JSONObject imageData, String title) {
        JSONArray backdrops = imageData.optJSONArray("backdrops");
        if (backdrops == null || backdrops.isEmpty()) return List.of();

        String safeTitle = title.replaceAll("[\\/:*?\"<>|]", "_");
        Path folderPath = Paths.get(safeTitle);
        List<String> imagePaths = new ArrayList<>();

        try {
            Files.createDirectories(folderPath);

            // Download up to 3 images
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

    /**
     * Downloads the content from the given URL and writes it to the specified file.
     *
     * @param url        the source URL of the image
     * @param outputPath the path where the image should be saved
     * @throws IOException if the download or write operation fails
     */
    private void downloadToFile(URL url, Path outputPath) throws IOException {
        try (InputStream in = new BufferedInputStream(url.openStream());
             OutputStream out = new FileOutputStream(outputPath.toFile())) {
            in.transferTo(out); // Java 9+ simplifies stream copying
        }
    }
}