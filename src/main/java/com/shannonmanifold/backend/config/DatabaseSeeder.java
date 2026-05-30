package com.shannonmanifold.backend.config;

import com.shannonmanifold.backend.entity.Tutorial;
import com.shannonmanifold.backend.entity.TutorialLevel;
import com.shannonmanifold.backend.entity.TutorialStep;
import com.shannonmanifold.backend.repository.TutorialRepository;
import com.shannonmanifold.backend.repository.TutorialStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final TutorialRepository tutorialRepository;
    private final TutorialStepRepository tutorialStepRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. 집합론 (Set Theory) 튜토리얼 생성
        if (!tutorialRepository.existsByTitle("집합론 (Set Theory)")) {
            log.info("집합론 튜토리얼 데이터가 없어 생성합니다.");
            Tutorial setTheory = Tutorial.builder()
                    .title("집합론 (Set Theory)")
                    .description("수학의 기초가 되는 집합론의 기본 개념과 논리 기호를 학습하고 Lean 4를 통해 증명해봅니다.")
                    .level(TutorialLevel.Beginner)
                    .duration("2주")
                    .lessonsCount(2)
                    .icon("∀")
                    .authorName("ShannonManifold")
                    .updatedAt(LocalDate.now())
                    .prerequisitesJson("[]")
                    .tagsJson("[\"Logic\", \"Set Theory\", \"Lean 4\"]")
                    .build();
            setTheory = tutorialRepository.save(setTheory);

            TutorialStep setStep1 = TutorialStep.builder()
                    .tutorialId(setTheory.getId())
                    .stepOrder(1)
                    .title("집합의 상등 (Set Equality)")
                    .description("두 집합 A, B가 서로의 부분집합이면 두 집합은 같습니다.")
                    .explanation("집합론에서 두 집합이 같다는 것은 원소가 정확히 일치한다는 것을 의미합니다. Lean 4에서는 `ext` 전술을 사용하여 원소 수준으로 분해해 증명할 수 있습니다.")
                    .starterCode("import Mathlib.Data.Set.Basic\n\nvariable {α : Type*} (A B : Set α)\n\ntheorem set_ext_iff : A = B ↔ (∀ x, x ∈ A ↔ x ∈ B) := by\n  sorry")
                    .solution("import Mathlib.Data.Set.Basic\n\nvariable {α : Type*} (A B : Set α)\n\ntheorem set_ext_iff : A = B ↔ (∀ x, x ∈ A ↔ x ∈ B) := by\n  exact Set.ext_iff")
                    .hint("Set.ext_iff 정리(theorem)를 활용해 보세요.")
                    .build();

            TutorialStep setStep2 = TutorialStep.builder()
                    .tutorialId(setTheory.getId())
                    .stepOrder(2)
                    .title("공집합의 유일성 (Uniqueness of Empty Set)")
                    .description("원소를 가지지 않는 집합은 유일합니다.")
                    .explanation("원소를 전혀 가지지 않는 집합은 공집합(empty set) 하나뿐입니다. 이를 수학적으로 귀류법이나 외연성 공리를 사용해 엄밀히 증명합니다.")
                    .starterCode("import Mathlib.Data.Set.Basic\n\nvariable {α : Type*}\n\ntheorem empty_unique (S : Set α) (h : ∀ x, x ∉ S) : S = ∅ := by\n  sorry")
                    .solution("import Mathlib.Data.Set.Basic\n\nvariable {α : Type*}\n\ntheorem empty_unique (S : Set α) (h : ∀ x, x ∉ S) : S = ∅ := by\n  ext x\n  simp [h]")
                    .hint("`ext x`를 통해 원소 단위로 분해한 후, `h` 가설과 공집합의 성질을 단순화(`simp`)해 보세요.")
                    .build();

            tutorialStepRepository.saveAll(List.of(setStep1, setStep2));
            log.info("집합론 튜토리얼 데이터 생성 완료");
        }

        // 2. 해석학 (Mathematical Analysis) 튜토리얼 생성
        if (!tutorialRepository.existsByTitle("해석학 (Mathematical Analysis)")) {
            log.info("해석학 튜토리얼 데이터가 없어 생성합니다.");
            Tutorial analysis = Tutorial.builder()
                    .title("해석학 (Mathematical Analysis)")
                    .description("실수의 완비성 공리, 수열의 극한, 그리고 함수의 연속성을 Lean 4로 엄밀하게 정의하고 증명합니다.")
                    .level(TutorialLevel.Intermediate)
                    .duration("4주")
                    .lessonsCount(2)
                    .icon("∫")
                    .authorName("ShannonManifold")
                    .updatedAt(LocalDate.now())
                    .prerequisitesJson("[\"집합론\"]")
                    .tagsJson("[\"Analysis\", \"Calculus\", \"Lean 4\"]")
                    .build();
            analysis = tutorialRepository.save(analysis);

            TutorialStep analysisStep1 = TutorialStep.builder()
                    .tutorialId(analysis.getId())
                    .stepOrder(1)
                    .title("수열의 극한 (Limit of Sequence)")
                    .description("ε-N 법을 이용하여 수열의 극한을 정의하고 증명합니다.")
                    .explanation("해석학에서 가장 기본이 되는 입실론-N(ε-N) 논법을 사용하여 수열의 수렴을 엄밀히 정의합니다.")
                    .starterCode("import Mathlib.Data.Real.Basic\n\ndef ConvergesTo (a : ℕ → ℝ) (L : ℝ) : Prop :=\n  ∀ ε > 0, ∃ N : ℕ, ∀ n ≥ N, |a n - L| < ε")
                    .solution("import Mathlib.Data.Real.Basic\n\ndef ConvergesTo (a : ℕ → ℝ) (L : ℝ) : Prop :=\n  ∀ ε > 0, ∃ N : ℕ, ∀ n ≥ N, |a n - L| < ε")
                    .hint("수열의 극한 정의를 명확히 이해하고 다음 단계로 진행하세요.")
                    .build();

            TutorialStep analysisStep2 = TutorialStep.builder()
                    .tutorialId(analysis.getId())
                    .stepOrder(2)
                    .title("함수의 연속성 (Continuity of Function)")
                    .description("ε-δ 법을 이용하여 점에서의 연속성을 증명합니다.")
                    .explanation("임의의 입실론(ε)에 대해 적절한 델타(δ)가 존재하여 함수 값이 ε 내로 수렴함을 입실론-델타 논법으로 보입니다.")
                    .starterCode("import Mathlib.Data.Real.Basic\n\ndef ContinuousAt (f : ℝ → ℝ) (x₀ : ℝ) : Prop :=\n  ∀ ε > 0, ∃ δ > 0, ∀ x, |x - x₀| < δ → |f x - f x₀| < ε")
                    .solution("import Mathlib.Data.Real.Basic\n\ndef ContinuousAt (f : ℝ → ℝ) (x₀ : ℝ) : Prop :=\n  ∀ ε > 0, ∃ δ > 0, ∀ x, |x - x₀| < δ → |f x - f x₀| < ε")
                    .hint("함수의 연속성 정의를 익히고 증명 실습을 진행해 보세요.")
                    .build();

            tutorialStepRepository.saveAll(List.of(analysisStep1, analysisStep2));
            log.info("해석학 튜토리얼 데이터 생성 완료");
        }

        // 3. 위상수학 (Topology) 튜토리얼 생성
        if (!tutorialRepository.existsByTitle("위상수학 (Topology)")) {
            log.info("위상수학 튜토리얼 데이터가 없어 생성합니다.");
            Tutorial topology = Tutorial.builder()
                    .title("위상수학 (Topology)")
                    .description("열린 집합, 닫힌 집합, 그리고 위상 공간에서의 연속 사상의 개념을 추상화하여 학습합니다.")
                    .level(TutorialLevel.Advanced)
                    .duration("6주")
                    .lessonsCount(2)
                    .icon("τ")
                    .authorName("ShannonManifold")
                    .updatedAt(LocalDate.now())
                    .prerequisitesJson("[\"집합론\", \"해석학\"]")
                    .tagsJson("[\"Topology\", \"Geometry\", \"Lean 4\"]")
                    .build();
            topology = tutorialRepository.save(topology);

            TutorialStep topologyStep1 = TutorialStep.builder()
                    .tutorialId(topology.getId())
                    .stepOrder(1)
                    .title("위상 공간의 정의 (Definition of Topological Space)")
                    .description("위상 공간의 세 가지 공리를 Lean 4로 확인합니다.")
                    .explanation("위상 공간(Topological Space)은 공집합과 전체집합을 포함하며, 유한 교집합과 임의의 합집합에 대해 닫혀 있는 구조입니다.")
                    .starterCode("import Mathlib.Topology.Basic\n\n-- 위상수학의 기본 개념을 Lean 4 표준 라이브러리로 탐색합니다.")
                    .solution("import Mathlib.Topology.Basic\n\n-- 위상수학의 기본 개념을 Lean 4 표준 라이브러리로 탐색합니다.")
                    .hint("Mathlib의 TopologicalSpace 정의를 찾아 참고해 보세요.")
                    .build();

            TutorialStep topologyStep2 = TutorialStep.builder()
                    .tutorialId(topology.getId())
                    .stepOrder(2)
                    .title("연속 함수의 위상적 정의 (Topological Continuous Function)")
                    .description("역상이 열린 집합이면 연속 함수임을 증명합니다.")
                    .explanation("위상적 연속성의 일반적인 정의는 '열린 집합의 역상(preimage)은 열린 집합이다'입니다. 이를 입실론-델타의 일반화로 이해합니다.")
                    .starterCode("import Mathlib.Topology.Basic\n\n-- 연속 함수(Continuous)의 정의와 성질을 파악합니다.")
                    .solution("import Mathlib.Topology.Basic\n\n-- 연속 함수(Continuous)의 정의와 성질을 파악합니다.")
                    .hint("`Continuous` 타입의 정의가 `isOpen` 역상 보존과 일치함을 확인해 보세요.")
                    .build();

            tutorialStepRepository.saveAll(List.of(topologyStep1, topologyStep2));
            log.info("위상수학 튜토리얼 데이터 생성 완료");
        }
    }
}
