package pluto.upik.domain.vote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.user.data.model.User;
import pluto.upik.domain.user.repository.UserRepository;
import pluto.upik.domain.vote.data.DTO.CreateVoteInput;
import pluto.upik.domain.vote.data.DTO.OptionWithStatsPayload;
import pluto.upik.domain.vote.data.DTO.VoteDetailPayload;
import pluto.upik.domain.vote.data.DTO.VotePayload;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final UserRepository userRepository;

    public VotePayload createVote(CreateVoteInput input) {
        // 1. Vote 엔티티 생성
        Vote vote = Vote.builder()
                .id(UUID.randomUUID())
                .question(input.getTitle())
                .category(input.getCategory())
                .status(Vote.Status.OPEN)
                .finishedAt(LocalDate.now().plusDays(3)) // 예: 3일 뒤 종료
                .build();

        // 2. Vote 저장
        Vote savedVote = voteRepository.save(vote);

        // 3. Option들 생성
        List<Option> options = input.getOptions().stream().map(content ->
                Option.builder()
                        .id(UUID.randomUUID())
                        .vote(savedVote)
                        .content(content)
                        .build()
        ).toList();

        // 4. Option들 저장
        List<Option> savedOptions = optionRepository.saveAll(options);

        // 5. 정적 팩토리 메서드 사용하여 VotePayload 반환
        return VotePayload.fromEntity(savedVote, savedOptions);
    }

    @Transactional(readOnly = true)
    public List<VotePayload> getAllVotes() {
        List<Vote> votes = voteRepository.findAll();
        List<VotePayload> votePayloads = new ArrayList<>();

        for (Vote vote : votes) {
            List<Option> options = optionRepository.findByVoteId(vote.getId());
            Long totalResponses = voteResponseRepository.countByVoteId(vote.getId());
        List<OptionWithStatsPayload> optionStats = new ArrayList<>();
        for (Option option : options) {
            Long optionCount = voteResponseRepository.countByOptionId(option.getId());
            float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

            optionStats.add(new OptionWithStatsPayload(
                option.getId(),
                option.getContent(),
                optionCount.intValue(),
                percentage
            ));
        }

            votePayloads.add(VotePayload.fromEntityWithStats(
                vote,
                options,
                optionStats,
                totalResponses.intValue()
            ));
        }

        return votePayloads;
    }

    @Transactional(readOnly = true)
    public VoteDetailPayload getVoteById(UUID voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("투표를 찾을 수 없습니다: " + voteId));

        List<Option> options = optionRepository.findByVoteId(voteId);
        Long totalResponses = voteResponseRepository.countByVoteId(voteId);

        List<OptionWithStatsPayload> optionStats = new ArrayList<>();
        for (Option option : options) {
            Long optionCount = voteResponseRepository.countByOptionId(option.getId());
            float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

            optionStats.add(new OptionWithStatsPayload(
                option.getId(),
                option.getContent(),
                optionCount.intValue(),
                percentage
            ));
        }

        String creatorName = null;
        if (vote.getUser() != null) {
            User creator = userRepository.findById(vote.getUser().getId())
                    .orElse(null);
            if (creator != null) {
                creatorName = creator.getUsername();
            }
        }

        return VoteDetailPayload.builder()
                .id(vote.getId())
                .title(vote.getQuestion())
                .category(vote.getCategory())
                .status(vote.getStatus().name())
                .createdBy(creatorName)
                .finishedAt(vote.getFinishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .totalResponses(totalResponses.intValue())
                .options(optionStats)
                .build();
    }
}