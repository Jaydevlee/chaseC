package com.example.chaseC.controller;

import com.example.chaseC.service.CustomsService;
import com.example.chaseC.dto.TrackRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class CustomsApiClientController {
  private final CustomsService customsService;

  @PostMapping("/tracker")
  public ResponseEntity<TrackRequestDto> startTracker (@Valid @RequestBody TrackRequestDto trackRequestDto) {
    TrackRequestDto result = customsService.startTracker(trackRequestDto);
    return ResponseEntity.ok(result);
  }
}
