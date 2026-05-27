package com.shannonmanifold.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shannonmanifold.backend.dto.ChallengeDetailResponse;
import com.shannonmanifold.backend.dto.ChallengeResponse;
import com.shannonmanifold.backend.entity.Challenge;
import com.shannonmanifold.backend.entity.ChallengeDifficulty;
import com.shannonmanifold.backend.repository.ChallengeRepository;
import com.shannonmanifold.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public List<ChallengeResponse> getChallenges(String difficulty) {
        List<Challenge> challenges = (difficulty != null && !difficulty.isBlank())
                ? challengeRepository.findByDifficultyOrderByCreatedAtDesc(ChallengeDifficulty.valueOf(difficulty))
                : challengeRepository.findAllByOrderByCreatedAtDesc();
        return challenges.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ChallengeDetailResponse getChallenge(Long challengeId) {
        return toDetailResponse(findChallengeOrThrow(challengeId));
    }

    @Transactional
    public ChallengeDetailResponse sponsorChallenge(Long challengeId, String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));
        Challenge challenge = findChallengeOrThrow(challengeId);
        challenge.incrementBackers();
        return toDetailResponse(challenge);
    }

    private Challenge findChallengeOrThrow(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다. ID: " + challengeId));
    }

    private ChallengeResponse toResponse(Challenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .field(challenge.getField())
                .description(challenge.getDescription())
                .prize(challenge.getPrize())
                .sponsorPool(challenge.getSponsorPool())
                .backers(challenge.getBackers())
                .progress(challenge.getProgress())
                .difficulty(challenge.getDifficulty())
                .proofSystem(challenge.getProofSystem())
                .accent(challenge.getAccent())
                .build();
    }

    private ChallengeDetailResponse toDetailResponse(Challenge challenge) {
        return ChallengeDetailResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .field(challenge.getField())
                .description(challenge.getDescription())
                .prize(challenge.getPrize())
                .sponsorPool(challenge.getSponsorPool())
                .backers(challenge.getBackers())
                .progress(challenge.getProgress())
                .difficulty(challenge.getDifficulty())
                .proofSystem(challenge.getProofSystem())
                .accent(challenge.getAccent())
                .detailedDescription(challenge.getDetailedDescription())
                .references(parseReferences(challenge.getReferencesJson()))
                .build();
    }

    private List<Map<String, String>> parseReferences(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
