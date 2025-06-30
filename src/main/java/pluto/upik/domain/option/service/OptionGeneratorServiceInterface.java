package pluto.upik.domain.option.service;

import pluto.upik.domain.option.data.DTO.GenerateOptionsResponse;

/**
 * 선택지 생성 서비스 인터페이스
 * AI를 활용한 선택지 생성 기능을 제공합니다.
 */
public interface OptionGeneratorServiceInterface {
    
    /**
     * 제목에 맞는 선택지를 생성합니다.
     *
     * @param title 제목
     * @param count 생성할 선택지 개수
     * @return 생성된 선택지 응답
     */
    GenerateOptionsResponse generateOptions(String title, int count);
}