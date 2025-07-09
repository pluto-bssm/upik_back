package pluto.upik.shared.translation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * 번역 서비스
 * 한국어와 영어 간의 번역 기능을 제공합니다.
 */
@Service
@Slf4j
public class TranslationService {

    private final RestClient restClient;

    public TranslationService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.mymemory.translated.net")
                .build();
    }

    /**
     * 한국어 텍스트를 영어로 번역합니다.
     *
     * @param koreanText 번역할 한국어 텍스트
     * @return 번역된 영어 텍스트
     */
    public String translateKoreanToEnglish(String koreanText) {
        log.debug("한국어를 영어로 번역: {}", koreanText);
        try {
            return translate(koreanText, "ko", "en");
        } catch (Exception e) {
            log.error("한국어 번역 중 오류 발생", e);
            // 번역 실패 시 원본 텍스트 반환
            return koreanText;
        }
    }

    /**
     * 영어 텍스트를 한국어로 번역합니다.
     *
     * @param englishText 번역할 영어 텍스트
     * @return 번역된 한국어 텍스트
     */
    public String translateEnglishToKorean(String englishText) {
        log.debug("영어를 한국어로 번역: {}", englishText);
        try {
            return translate(englishText, "en", "ko");
        } catch (Exception e) {
            log.error("영어 번역 중 오류 발생", e);
            // 번역 실패 시 원본 텍스트 반환
            return englishText;
        }
    }

    /**
     * 텍스트를 지정된 언어로 번역합니다.
     *
     * @param text 번역할 텍스트
     * @param sourceLang 원본 언어 코드
     * @param targetLang 대상 언어 코드
     * @return 번역된 텍스트
     */
    private String translate(String text, String sourceLang, String targetLang) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/get")
                            .queryParam("q", text)
                            .queryParam("langpair", sourceLang + "|" + targetLang)
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("responseData")) {
                Map<String, Object> responseData = (Map<String, Object>) response.get("responseData");
                if (responseData != null && responseData.containsKey("translatedText")) {
                    String translatedText = (String) responseData.get("translatedText");
                    log.debug("번역 완료: {}", translatedText);
                    return translatedText;
                }
            }
            
            log.error("번역 응답이 올바르지 않습니다: {}", response);
            return text; // 번역 실패 시 원본 텍스트 반환
        } catch (Exception e) {
            log.error("번역 API 호출 중 오류 발생", e);
            return text; // 번역 실패 시 원본 텍스트 반환
        }
    }
}