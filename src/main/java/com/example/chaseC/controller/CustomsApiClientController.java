package com.example.chaseC.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class CustomsApiClientController {
  private final CustomsApiClientController customsApiClient;

  @GetMapping("/find")
  public ResponseEntity<String> find (@RequestParam String hblNo) {
    return customsApiClient.find(hblNo);
  }
}
