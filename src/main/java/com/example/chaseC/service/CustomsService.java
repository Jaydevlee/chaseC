package com.example.chaseC.service;

import com.example.chaseC.entity.TrackRequest;
import dto.TrackRequestDto;

public interface CustomsService {
  TrackRequest createTracker(TrackRequestDto trackRequestDto);
}
