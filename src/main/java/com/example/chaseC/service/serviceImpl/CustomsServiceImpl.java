package com.example.chaseC.service.serviceImpl;

import com.example.chaseC.entity.TrackRequest;
import com.example.chaseC.repository.TrackRequestRepository;
import com.example.chaseC.service.CustomsApiClient;
import com.example.chaseC.service.CustomsService;
import dto.TrackRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomsServiceImpl implements CustomsService {
  private final TrackRequestRepository trackRequestRepository;
  private final CustomsApiClient customsApiClient;

  @Override
  @Transactional
  public TrackRequest createTracker(TrackRequestDto trackRequestDto) {
    TrackRequest trackRequest = TrackRequest.builder()
          .id(trackRequestDto.getId())
          .hblNo(trackRequestDto.getHblNo())
          .email(trackRequestDto.getEmail())
          .createdAt(trackRequestDto.getCreatedAt())
          .blYear(trackRequestDto.getBlYear())
          .updatedAt(trackRequestDto.getUpdatedAt())
          .build();
    TrackRequest savedRequest = trackRequestRepository.save(trackRequest);

    String currentStatus = customsApiClient.getStatus(savedRequest.getHblNo());
    savedRequest.updateStatus(currentStatus);
    return savedRequest;
  }
}
