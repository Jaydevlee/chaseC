package com.example.chaseC.service.serviceImpl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.chaseC.service.CustomsApiClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("dev")
public class MockClientApiService implements CustomsApiClient {
   private int call = 0;

   @Override
   public String getStatus(String hblNo){
      call++;
      String result = "";
      if(call <= 1) result = "입항적하목록 제출";
      if(call == 2) result = "입항적하목록 심사완료";
      if(call == 3) result = "통관목록접수";
      if(call == 4) result = "입항보고 제출";
      if(call == 5) result = "입항보고 승인";
      if(call == 6) result = "하기신고 수리";
      if(call == 7) result = "반입신고";
      if(call == 8) result = "통관목록심사완료";
      if(call == 9) result = "반출신고";
      return result;
   }
}
