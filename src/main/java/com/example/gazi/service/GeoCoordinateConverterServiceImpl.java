package com.example.gazi.service;

import org.osgeo.proj4j.*;
import org.springframework.stereotype.Service;

@Service
public class GeoCoordinateConverterServiceImpl implements GeoCoordinateConverterService {
    @Override
    public ProjCoordinate grs80ToWgs84(double x, double y) {
        // Create GRS80TM Coordinate Reference System
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem grs80tmCRS = crsFactory.createFromName("EPSG:5181");

        // Create Google Maps Coordinate Reference System
        CoordinateReferenceSystem googleMapsCRS = crsFactory.createFromName("EPSG:4326");

        // Create coordinate transform factory
        CoordinateTransformFactory transformFactory = new CoordinateTransformFactory();

        // Create coordinate transform
        CoordinateTransform transform = transformFactory.createTransform(grs80tmCRS, googleMapsCRS);

        // Perform coordinate conversion
        ProjCoordinate grs80tmCoord = new ProjCoordinate(x, y);
        ProjCoordinate googleMapsCoord = new ProjCoordinate();

        return transform.transform(grs80tmCoord, googleMapsCoord);
    }
}
