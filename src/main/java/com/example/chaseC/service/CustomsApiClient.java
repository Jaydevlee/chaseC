package com.example.chaseC.service;

import java.util.List;
import java.util.Map;

public interface CustomsApiClient {

    List<Map<String, String>> getTrackInfo (String hblNo, int blYear);
}