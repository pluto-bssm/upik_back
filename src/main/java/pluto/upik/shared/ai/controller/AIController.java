package pluto.upik.shared.ai.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pluto.upik.shared.ai.data.DTO.*;
import pluto.upik.shared.ai.service.AIService;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI", description = "AI 관련 API")
public class AIController {

    private final AIService aiService;

    @PostMapping("/ask")
    @Operation(summary = "AI에게 질문하기", description = "DeepSeek AI에게 질문하고 답변을 받습니다.")
    @ApiResponse(responseCode = "200", description = "질문 성공")
    public ResponseEntity<String> askQuestion(@Valid @RequestBody AIQuestionRequest request) {
        try {
            String response = aiService.askToDeepSeekAI(request.getQuestion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("AI 질문 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("AI 서비스 처리 중 오류가 발생했습니다");
        }
    }



    @PostMapping("/auto")
    public ResponseEntity<GuideResponseDTO> generateGuide(@RequestBody GuideRequestDTO requestDto) {
        GuideResponseDTO responseDto = aiService.generateGuide(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
