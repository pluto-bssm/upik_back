package pluto.upik.domain.voteResponse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.user.data.model.User;
import pluto.upik.domain.user.repository.UserRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.data.DTO.CreateVoteResponseInput;
import pluto.upik.domain.voteResponse.data.DTO.VoteResponsePayload;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteResponseService {

    private final VoteResponseRepository voteResponseRepository;
    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean hasUserVoted(UUID userId, UUID voteId) {
        return voteResponseRepository.findByUserIdAndVoteId(userId, voteId).isPresent();
    }

    public VoteResponsePayload createVoteResponse(CreateVoteResponseInput input, UUID userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 투표 조회
        Vote vote = voteRepository.findById(input.getVoteId())
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다: " + input.getVoteId()));

        // 3. 옵션 조회
        Option option = optionRepository.findById(input.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다: " + input.getOptionId()));

        // 4. 투표 상태 확인
        if (vote.getStatus() != Vote.Status.OPEN) {
            throw new IllegalStateException("투표가 종료되었습니다.");
        }

        // 5. 옵션이 해당 투표에 속하는지 확인
        if (!option.getVote().getId().equals(vote.getId())) {
            throw new IllegalArgumentException("해당 옵션은 이 투표에 속하지 않습니다.");
        }

        // 6. 중복 투표 확인
        Optional<VoteResponse> existingResponse = voteResponseRepository
                .findByUserIdAndVoteId(userId, input.getVoteId());

        if (existingResponse.isPresent()) {
            throw new IllegalStateException("이미 이 투표에 참여하셨습니다.");
        }

        // 7. VoteResponse 생성 및 저장
        VoteResponse voteResponse = VoteResponse.builder()
                .id(UUID.randomUUID())
                .user(user)
                .vote(vote)
                .selectedOption(option)
                .createdAt(LocalDate.now())
                .build();

        VoteResponse savedVoteResponse = voteResponseRepository.save(voteResponse);

        // 8. 응답 반환
        return VoteResponsePayload.fromEntity(savedVoteResponse);
    }

    @Transactional(readOnly = true)
    public Long getVoteResponseCount(UUID voteId) {
        return voteResponseRepository.countByVoteId(voteId);
    }

    @Transactional(readOnly = true)
    public Long getOptionResponseCount(UUID optionId) {
        return voteResponseRepository.countByOptionId(optionId);
    }
}
