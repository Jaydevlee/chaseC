package com.example.chaseC.controller;

import com.example.chaseC.entity.TrackRequest;
import com.example.chaseC.service.CustomsApiClient;
import com.example.chaseC.service.CustomsService;
import com.example.chaseC.service.serviceImpl.CustomsServiceImpl;
import dto.TrackRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class CustomsApiClientController {
  private final CustomsService customsService;

  @PostMapping("/create_tracker")
  public ResponseEntity<TrackRequest> createTracker (@RequestBody TrackRequestDto trackRequestDto) {
    TrackRequest result = customsService.createTracker(trackRequestDto);
    return ResponseEntity.ok(result);
  }
}
