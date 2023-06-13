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
    public  Map<String, Object> mapSnapshot(double latitude, double longitude,Long headKeywordId) {
        // Google Maps Static API endpoint
        String staticMapsEndpoint = "https://maps.googleapis.com/maps/api/staticmap";

        // Zoom level for the map
        int zoom = 16;

        try {

            // Image size and scale
            int width = 300;
            int height = 300;
            int scale = 1;

            // marker
            StringBuilder marker = new StringBuilder();
            marker.append("&markers=icon:");
//&markers=icon:https://gazimapbucket.s3.ap-northeast-2.amazonaws.com/marker/공사.png|37.555819,126.971409

            if (headKeywordId == 1L) {
                marker.append("https://gazimapbucket.s3.ap-northeast-2.amazonaws.com/marker/%E1%84%89%E1%85%B5%E1%84%8B%E1%85%B1.png");
            }
            else if (headKeywordId == 3L) {
                marker.append("https://gazimapbucket.s3.ap-northeast-2.amazonaws.com/marker/%E1%84%8C%E1%85%A1%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%8C%E1%85%A2%E1%84%92%E1%85%A2.png");
            }
            else if (headKeywordId == 4L) {
                marker.append("https://gazimapbucket.s3.ap-northeast-2.amazonaws.com/marker/%E1%84%80%E1%85%A9%E1%86%BC%E1%84%89%E1%85%A1.png");
            }
            else if (headKeywordId == 8L) {
                marker.append("https://gazimapbucket.s3.ap-northeast-2.amazonaws.com/marker/%E1%84%92%E1%85%A2%E1%86%BC%E1%84%89%E1%85%A1.png");
            }
            else if (headKeywordId == 9L) {
                marker.append("https://gazimapbucket.s3.ap-northeast-2.amazonaws.com/marker/%E1%84%80%E1%85%B5%E1%84%90%E1%85%A1.png");
            }

            marker.append("|");
            marker.append(latitude);
            marker.append(",");
            marker.append(longitude);



            // map style
            StringBuilder styleBuilder = new StringBuilder();
            styleBuilder.append("&style=feature:all|element:labels.text.fill|color:0x333333|saturation:36|lightness:40")
                    .append("&style=feature:all|element:labels.text.stroke|color:0xFFFFFF|visibility:on|lightness:16")
                    .append("&style=feature:all|element:labels.icon|visibility:off")
                    .append("&style=feature:water|element:geometry|color:0xBDDFF4|lightness:17")
                    .append("&style=feature:landscape|element:geometry|color:0xF2F1F5|lightness:20")
                    .append("&style=feature:road|element:labels.text|visibility:off")
                    .append("&style=feature:road.highway|element:geometry.fill|color:0xC8BED0|lightness:17")
                    .append("&style=feature:road.highway|element:geometry.stroke|color:0xFFFFFF|lightness:29|weight:0.2")
                    .append("&style=feature:road.arterial|element:geometry|color:0xD7D1E1|lightness:18")
                    .append("&style=feature:road.local|element:geometry|weight:1.00")
                    .append("&style=feature:poi|element:all|color:0x6D6971|visibility:simplified")
                    .append("&style=feature:poi|element:geometry|color:0xE8E7E8|lightness:21")
                    .append("&style=feature:poi.business|element:all|visibility:off")
                    .append("&style=feature:poi.park|element:all|color:0xD9E8E2|lightness:21")
                    .append("&style=feature:transit|element:geometry|color:0xF2F2F2|lightness:19")
                    .append("&style=feature:transit.station.bus|element:all|color:0x6D6971")
                    .append("&style=feature:transit.station.rail|element:all|color:0x532EBF|visibility:simplified")
                    .append("&style=feature:administrative|element:geometry.fill|color:0xFEFEFE|lightness:20")
                    .append("&style=feature:administrative|element:geometry.stroke|color:0xFEFEFE|lightness:17|weight:1.2");



            // Construct the URL for the map image
            String imageUrl = staticMapsEndpoint + "?center=" + latitude + "," + longitude + "&zoom=" + zoom +
                    "&size=" + width + "x" + height + marker+"&scale=" + scale + styleBuilder + "&key=" + apiKey;

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
