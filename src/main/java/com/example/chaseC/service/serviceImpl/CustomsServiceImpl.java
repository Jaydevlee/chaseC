package com.example.chaseC.service.serviceImpl;

import com.example.chaseC.dto.MailSendDto;
import com.example.chaseC.entity.TrackHistory;
import com.example.chaseC.entity.TrackRequest;
import com.example.chaseC.repository.TrackHistoryRepository;
import com.example.chaseC.repository.TrackRequestRepository;
import com.example.chaseC.service.CustomsApiClient;
import com.example.chaseC.service.CustomsService;
import com.example.chaseC.dto.TrackHistoryDto;
import com.example.chaseC.dto.TrackRequestDto;
import com.example.chaseC.service.MailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomsServiceImpl implements CustomsService {
  private final TrackRequestRepository trackRequestRepository;
  private final TrackHistoryRepository trackHistoryRepository;
  private final CustomsApiClient customsApiClient;
  private final MailService mailService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

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
      List<Map<String, String>> historyList = customsApiClient.getTrackInfo(trackRequest.getHblNo(), trackRequest.getBlYear());

      saveAllHistory(trackRequest, historyList);
      // 상태가 변경되었으면 업데이트 및 히스토리 저장
      if (!historyList.isEmpty()) {
          String latestStatus = historyList.get(0).get("status");
          if (!latestStatus.equals(trackRequest.getStatus())) {
              trackRequest.updateStatus(latestStatus);
          }
      }

      return mapToDto(trackRequest);
  }

 // 로직 내부에서 값이 저장되기 때문에 DTO를 거치지 않음
 // DB에 존재하지 않는 데이터일 경우에만 저장
 private void saveAllHistory(TrackRequest trackRequest, List<Map<String, String>> historyList) {
     for (Map<String, String> item : historyList) {
         String status = item.get("status");
         String timeStr = item.get("processTime");

         // 시간 파싱 (예외 처리 필요할 수 있음)
         LocalDateTime processTime;
         try {
             processTime = LocalDateTime.parse(timeStr, formatter);
         } catch (Exception e) {
             processTime = LocalDateTime.now(); // 파싱 실패 시 현재 시간
         }

         // DB에 동일한 상태 + 동일한 시간이 있는지 확인
         LocalDateTime finalProcessTime = processTime;
         boolean exists = trackRequest.getTrackHistory().stream()
                 .anyMatch(h -> h.getStatus().equals(status) && h.getProcessingTime().isEqual(finalProcessTime));

         if (!exists) {
             TrackHistory newHistory = TrackHistory.builder()
                     .status(status)
                     .processingTime(processTime)
                     .trackRequest(trackRequest)
                     .build();

             trackHistoryRepository.save(newHistory);
             trackRequest.getTrackHistory().add(newHistory);
         }
     }
 }


    @Scheduled(fixedRate = 360000)
  @Transactional
  public void updateStatus() {
    List<TrackRequest> allRequests = trackRequestRepository.findAll();

    for (TrackRequest trackRequest : allRequests) {
      if("반출신고".equals(trackRequest.getStatus()) || "통관목록심사완료".equals(trackRequest.getStatus())) continue;

      List<Map<String, String>> historyList = customsApiClient.getTrackInfo(trackRequest.getHblNo(), trackRequest.getBlYear());

      if (historyList == null || historyList.isEmpty()) {
          log.warn("API 조회 실패 (데이터 없음): {}", trackRequest.getHblNo());
          continue;
      }

        String latestApiStatus = historyList.get(0).get("status");

      if (!latestApiStatus.equals(trackRequest.getStatus())) {
        log.info("상태 변경 [{}]: {} -> {}", trackRequest.getHblNo(), trackRequest.getStatus(), latestApiStatus);
        saveAllHistory(trackRequest, historyList);
        trackRequest.updateStatus(latestApiStatus);

        if(trackRequest.getEmail() != null && !trackRequest.getEmail().isEmpty()) {
          MailSendDto mailDto = MailSendDto.builder()
                  .toEmail(trackRequest.getEmail())
                  .hblNo(trackRequest.getHblNo())
                  .newStatus(latestApiStatus)
                  .build();
          mailService.sendStatusUpdateEmail(mailDto);
        }
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
