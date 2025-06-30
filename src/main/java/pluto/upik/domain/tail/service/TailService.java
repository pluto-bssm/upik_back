package pluto.upik.domain.tail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.tail.data.DTO.TailPayload;
import pluto.upik.domain.tail.data.DTO.TailResponsePayload;
import pluto.upik.domain.tail.data.model.Tail;
import pluto.upik.domain.tail.data.model.TailResponse;
import pluto.upik.domain.tail.repository.TailRepository;
import pluto.upik.domain.tail.repository.TailResponseRepository;
import pluto.upik.domain.user.data.model.User;
import pluto.upik.domain.user.repository.UserRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TailService {

    private final TailRepository tailRepository;
    private final TailResponseRepository tailResponseRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    /**
     * 테일 생성
     *
     * @param voteIdStr 투표 ID 문자열
     * @param question 테일 질문
     * @return 생성된 테일 정보
     */
    public TailPayload createTail(String voteIdStr, String question) {
        log.debug("테일 생성 서비스 시작: voteId={}, question={}", voteIdStr, question);
        // voteId가 null이 아닌지 확인
        if (voteIdStr == null) {
            throw new IllegalArgumentException("투표 ID는 null일 수 없습니다.");
        }

        // 문자열 ID를 UUID로 변환
        UUID voteId;
        try {
            voteId = UUID.fromString(voteIdStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 투표 ID 형식입니다: " + voteIdStr);
        }

        // 투표 조회
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("투표를 찾을 수 없습니다: " + voteId));

        // 테일 생성
        Tail tail = Tail.builder()
                .id(UUID.randomUUID())
                .vote(vote)
                .question(question)
                .build();

        // 테일 저장
        Tail savedTail = tailRepository.save(tail);
        log.debug("테일 생성 완료: id={}", savedTail.getId());

        return TailPayload.fromEntity(savedTail);
    }

    /**
     * 테일 응답 생성
     *
     * @param tailIdStr 테일 ID 문자열
     * @param userIdStr 사용자 ID 문자열
     * @param answer 응답 내용
     * @return 생성된 테일 응답 정보
     */
    public TailResponsePayload createTailResponse(String tailIdStr, String userIdStr, String answer) {
        log.debug("테일 응답 생성 서비스 시작: tailId={}, userId={}, answer={}", tailIdStr, userIdStr, answer);

        // tailId와 userId가 null이 아닌지 확인
        if (tailIdStr == null) {
            throw new IllegalArgumentException("테일 ID는 null일 수 없습니다.");
        }
        if (userIdStr == null) {
            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");
        }

        // 문자열 ID를 UUID로 변환
        UUID tailId, userId;
        try {
            tailId = UUID.fromString(tailIdStr);
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 ID 형식입니다: " + e.getMessage());
        }

        // 테일 조회
        Tail tail = tailRepository.findById(tailId)
                .orElseThrow(() -> new ResourceNotFoundException("테일을 찾을 수 없습니다: " + tailId));

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 테일 응답 생성
        TailResponse tailResponse = TailResponse.builder()
                .id(UUID.randomUUID())
                .tail(tail)
                .user(user)
                .answer(answer)
                .build();

        // 테일 응답 저장
        TailResponse savedTailResponse = tailResponseRepository.save(tailResponse);
        log.debug("테일 응답 생성 완료: id={}", savedTailResponse.getId());

        return TailResponsePayload.fromEntity(savedTailResponse);
    }

    /**
     * 투표에 속한 모든 테일 조회
     *
     * @param voteId 투표 ID
     * @return 테일 목록
     */
    @Transactional(readOnly = true)
    public List<TailPayload> getTailsByVoteId(UUID voteId) {
        // 투표 존재 확인
        voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("투표를 찾을 수 없습니다: " + voteId));

        // 테일 목록 조회
        List<Tail> tails = tailRepository.findByVoteId(voteId);

        return tails.stream()
                .map(TailPayload::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 테일 응답 목록 조회
     *
     * @param tailId 테일 ID
     * @return 테일 응답 목록
     */
    @Transactional(readOnly = true)
    public List<TailResponsePayload> getTailResponsesByTailId(UUID tailId) {
        // 테일 존재 확인
        tailRepository.findById(tailId)
                .orElseThrow(() -> new ResourceNotFoundException("테일을 찾을 수 없습니다: " + tailId));

        // 테일 응답 목록 조회
        List<TailResponse> responses = tailResponseRepository.findByTailId(tailId);

        return responses.stream()
                .map(TailResponsePayload::fromEntity)
                .collect(Collectors.toList());
    }
}