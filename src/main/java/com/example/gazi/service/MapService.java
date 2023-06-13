package com.example.gazi.service;

import java.util.Map;

public interface MapService {
    Map<String, Object> mapSnapshot(double latitude, double longitude,Long headKeywordId);
}
