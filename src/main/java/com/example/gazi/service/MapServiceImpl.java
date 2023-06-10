package com.example.gazi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class MapServiceImpl implements MapService{

    @Value("${google.servicekey}")
    private String apiKey;

    @Override
    public  Map<String, Object> mapSnapshot(double latitude, double longitude) {
        // Google Maps Static API endpoint
        String staticMapsEndpoint = "https://maps.googleapis.com/maps/api/staticmap";

        // Zoom level for the map
        int zoom = 16;

        try {

            // Image size and scale
            int width = 300;
            int height = 300;
            int scale = 1;

            // Construct the URL for the map image
            String imageUrl = staticMapsEndpoint + "?center=" + latitude + "," + longitude + "&zoom=" + zoom +
                    "&size=" + width + "x" + height + "&scale=" + scale + "&key=" + apiKey;

            // Open a connection to the URL
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();

            // Read the image data into a byte array
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, bytesRead);
            }

            System.out.println("Map snapshot saved successfully.");
            System.out.println(url);
            byte[] imageData = byteBuffer.toByteArray();

            // Close the input stream
            inputStream.close();

            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("imageData", imageData);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
