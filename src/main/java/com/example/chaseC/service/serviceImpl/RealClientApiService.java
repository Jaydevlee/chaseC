package com.example.chaseC.service.serviceImpl;

import com.example.chaseC.service.CustomsApiClient;
import com.example.chaseC.service.CustomsService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Calendar;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class RealClientApiService implements CustomsApiClient {
    @Value("${apiKey}")
    private String apiKey;
    int year = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public String getStatus (String hblNo){
        if(apiKey == null){
            log.error("apiKey is null");
            return null;
        }

        try{
            URI uri = UriComponentsBuilder
                    .fromUriString("https://unipass.customs.go.kr:38010/ext/rest/" +
                            "cargCsclPrgsInfoQry/retrieveCargCsclPrgsInfo")
                    .queryParam("crCn", apiKey)
                    .queryParam("hblNo", hblNo)
                    .queryParam("blYy", year)
                    .build()
                    .toUri();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
