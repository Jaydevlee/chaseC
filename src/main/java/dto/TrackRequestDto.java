package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrackRequestDto {
  private Long id;
  @NotBlank(message = "송장번호를 입력해주세요.")
  private String hblNo;
  @NotBlank(message = "이메일을 입력해주세요.")
  @Pattern(regexp = "^[a-zA-Z0-9]{2,}(?:\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
          message = "유효한 이메일 주소를 입력해주세요.")
  private String email;
  private String status;
  private int blYear;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime createdAt;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime updatedAt;
  private List<TrackHistoryDto> trackHistory;
}
