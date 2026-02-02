package com.example.chaseC.service.serviceImpl;

import com.example.chaseC.service.CustomsApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;
import org.json.XML;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class RealClientApiService implements CustomsApiClient {
    @Value("${custom.apiKey}")
    private String apiKey;
    int year = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public String getStatus (String hblNo, int blYear) {
        if(apiKey == null){
            log.error("apiKey is null");
            return null;
        }

        try{
            URI uri = UriComponentsBuilder
                    .fromUriString("https://unipass.customs.go.kr:38010/ext/rest/" +
                            "cargCsclPrgsInfoQry/retrieveCargCsclPrgsInfo")
                    .queryParam("crkyCn", apiKey)
                    .queryParam("hblNo", hblNo)
                    .queryParam("blYy", blYear)
                    .build()
                    .toUri();
            RestClient restClient = RestClient.builder()
                .messageConverters(converters -> converters
                        .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)))
                .build();
            String xmlRes = restClient.get().uri(uri).retrieve().body(String.class);
            log.info("xml response: " + xmlRes);

            // xml to json 변환
            JSONObject json = XML.toJSONObject(xmlRes);

            if(!json.has("cargCsclPrgsInfoQryRtnVo")) return null;
            JSONObject root = json.getJSONObject("cargCsclPrgsInfoQryRtnVo");

            if(root.has("cargCsclPrgsInfoQryVo")){
                Object vo = root.get("cargCsclPrgsInfoQryVo");
                JSONObject latestItem = null;

                // 데이터가 여러개, 한개 인지 구분
                if(vo instanceof JSONArray){
                    // 데이터가 2개 이상이 경우 배열로 가져와서 가장 마지막 항목 선택
                    JSONArray list = (JSONArray) vo;
                    latestItem = list.getJSONObject(list.length() - 1);

                } else if(vo instanceof JSONObject){
                    // 데이터가 1개일 경우
                    latestItem = (JSONObject) vo;
                }
                // 상태값 추출
                if(latestItem != null){
                    String status = latestItem.optString("csclPrgsStts", "상태없음");
                    log.info("status: " + status);
                    return status;
                } else {
                    log.info("조회된 상태 정보가 없습니다.");
                }
            }
        } catch (Exception e) {
            log.error("API 연동 오류 {}", e.getMessage());
        }
        return null;
    }
}
