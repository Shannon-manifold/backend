package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofResponse {
  private Long id;
  private String title;
  private String description;
  private String status;
  private String prover;
  private String language;
  private int likes;
  private int comments;
  private String date;
}