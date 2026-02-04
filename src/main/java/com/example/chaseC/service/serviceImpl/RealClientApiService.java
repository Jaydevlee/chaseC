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
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class RealClientApiService implements CustomsApiClient {
    List<Map<String, String>> historyList = new ArrayList<>();
    @Value("${custom.apiKey}")
    private String apiKey;
    int year = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public List<Map<String, String>> getTrackInfo(String hblNo, int blYear) {
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
            log.info("xml response: {}", xmlRes);

            // xml to json 변환
            JSONObject json = XML.toJSONObject(xmlRes);

            if(!json.has("cargCsclPrgsInfoQryRtnVo")) return historyList;
            JSONObject root = json.getJSONObject("cargCsclPrgsInfoQryRtnVo");

            if(root.has("cargCsclPrgsInfoDtlQryVo")){
                Object vo = root.get("cargCsclPrgsInfoDtlQryVo");

                // 데이터가 여러개, 한개 인지 구분
                if(vo instanceof JSONArray) {
                    // 데이터가 2개 이상이 경우 배열로 가져오기
                    JSONArray jsonList = (JSONArray) vo;
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject item = jsonList.getJSONObject(i);
                        historyList.add(parseHistoryItem(item));
                    }
                } else if(vo instanceof JSONObject){
                    // 데이터가 1개일 경우
                    JSONObject item = (JSONObject) vo;
                    historyList.add(parseHistoryItem(item));
                }
                return historyList;
            }
        } catch (Exception e) {
            log.error("API 연동 오류 {}", e.getMessage());
        }
        return null;
    }

    private Map<String, String> parseHistoryItem(JSONObject item) {
        Map<String, String> historyMap = new HashMap<>();

        // 상태 (반출신고, 통관목록심사완료 등)
        String status = item.optString("cargTrcnRelaBsopTpcd", "상태없음");
        // 처리 일시 (20260204122515 형식)
        String processTime = item.optString("prcsDttm", "");

        historyMap.put("status", status);
        historyMap.put("processTime", processTime);

        return historyMap;
    }
}
