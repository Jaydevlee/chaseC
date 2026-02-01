package dto;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrackRequestDto {
  private Long id;
  @NotBlank(message = "송장번호를 입력해주세요.")
  private String hblNo;
  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;
  private String status;
  private int blYear;
  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
