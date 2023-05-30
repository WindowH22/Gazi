package com.example.gazi.service;

import org.osgeo.proj4j.ProjCoordinate;

public interface GeoCoordinateConverterService {
    ProjCoordinate grs80ToWgs84(double x, double y);
}
