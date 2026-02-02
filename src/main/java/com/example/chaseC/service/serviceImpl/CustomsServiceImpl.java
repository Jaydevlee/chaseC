package com.example.chaseC.service.serviceImpl;

import com.example.chaseC.entity.TrackHistory;
import com.example.chaseC.entity.TrackRequest;
import com.example.chaseC.repository.TrackHistoryRepository;
import com.example.chaseC.repository.TrackRequestRepository;
import com.example.chaseC.service.CustomsApiClient;
import com.example.chaseC.service.CustomsService;
import dto.TrackHistoryDto;
import dto.TrackRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomsServiceImpl implements CustomsService {
  private final TrackRequestRepository trackRequestRepository;
  private final TrackHistoryRepository trackHistoryRepository;
  private final CustomsApiClient customsApiClient;

  @Override
  @Transactional
  public TrackRequestDto startTracker(TrackRequestDto trackRequestDto) {
      // HBL번호로 기존 요청 조회
      TrackRequest trackRequest = trackRequestRepository.findByHblNo(trackRequestDto.getHblNo())
              .orElse(null);
      // 기존 요청이 없으면 새로 생성
      if (trackRequest == null) {
          trackRequest = TrackRequest.builder()
                  .id(trackRequestDto.getId())
                  .hblNo(trackRequestDto.getHblNo())
                  .email(trackRequestDto.getEmail())
                  .blYear(trackRequestDto.getBlYear())
                  .status("조회대기")
                  .build();
          trackRequest = trackRequestRepository.save(trackRequest);
      }
      // 현재 상태 조회
      String currentStatus = customsApiClient.getStatus(trackRequest.getHblNo());
      // 상태가 변경되었으면 업데이트 및 히스토리 저장
      if(!currentStatus.equals(trackRequest.getStatus())) {
            trackRequest.updateStatus(currentStatus);
            TrackHistory newHistory = saveHistory(trackRequest, currentStatus);
            trackRequest.getTrackHistory().add(newHistory);
      }
      return mapToDto(trackRequest);
  }

 //로직 내부에서 값이 저장되기 때문에 DTO를 거치지 않음
  private TrackHistory saveHistory(TrackRequest request, String status) {
    TrackHistory trackHistory = TrackHistory.builder()
            .status(status)
            .processingTime(LocalDateTime.now())
            .trackRequest(request)
            .build();
    trackHistoryRepository.save(trackHistory);
    return trackHistory;
  }

  @Scheduled(fixedRate = 60000)
  @Transactional
  public void updateStatus() {
    List<TrackRequest> allRequests = trackRequestRepository.findAll();

    for (TrackRequest trackRequest : allRequests) {
      if("반출신고".equals(trackRequest.getStatus())) break;

      String currentStatus = customsApiClient.getStatus(trackRequest.getHblNo());
      if (!currentStatus.equals(trackRequest.getStatus())) {
        log.info("상태 변경 [{}]: {} -> {}", trackRequest.getHblNo(), trackRequest.getStatus(), currentStatus);
        trackRequest.updateStatus(currentStatus);
      }
    }
  }

  public TrackRequestDto mapToDto(TrackRequest trackRequest) {
    List<TrackHistoryDto> historyDto = trackRequest.getTrackHistory().stream()
            .map(this::mapToHistoryDto)
            .sorted(Comparator.comparing(TrackHistoryDto::getProcessingTime).reversed())
            .toList();

    return TrackRequestDto.builder()
          .id(trackRequest.getId())
          .hblNo(trackRequest.getHblNo())
          .email(trackRequest.getEmail())
          .createdAt(trackRequest.getCreatedAt())
          .blYear(trackRequest.getBlYear())
          .updatedAt(trackRequest.getUpdatedAt())
          .status(trackRequest.getStatus())
          .trackHistory(historyDto)
          .build();
  }

  public TrackHistoryDto mapToHistoryDto(TrackHistory trackHistory) {
    return TrackHistoryDto.builder()
            .id(trackHistory.getId())
            .status(trackHistory.getStatus())
            .processingTime(trackHistory.getProcessingTime())
            .build();
  }
}
