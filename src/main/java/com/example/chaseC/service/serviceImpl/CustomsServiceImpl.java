package com.example.chaseC.service.serviceImpl;

import com.example.chaseC.entity.TrackRequest;
import com.example.chaseC.repository.TrackRequestRepository;
import com.example.chaseC.service.CustomsApiClient;
import com.example.chaseC.service.CustomsService;
import dto.TrackRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

  @Scheduled(fixedRate = 60000)
  @Transactional
  public void updateStatus() {
    List<TrackRequest> allRequests = trackRequestRepository.findAll();

    for (TrackRequest trackRequest : allRequests) {
      if("반출신고".equals(trackRequest.getStatus())) continue;

      String currentStatus = customsApiClient.getStatus(trackRequest.getHblNo());
      if (!currentStatus.equals(trackRequest.getStatus())) {
        log.info("✨ 상태 변경! [{}]: {} -> {}", trackRequest.getHblNo(), trackRequest.getStatus(), currentStatus);
        trackRequest.updateStatus(currentStatus);
      }
    }
  }
}
