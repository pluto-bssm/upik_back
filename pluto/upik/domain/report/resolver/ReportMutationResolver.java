                    .success(false)
                    .message("질문 신고 거부 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 질문 신고를 수락하는 GraphQL 리졸버 메서드
     *
     * @param questionId 신고 대상 질문 ID
     * @param userId 처리하는 사용자 ID (선택적)
     * @return 질문 신고 수락 결과
     */
    @SchemaMapping(typeName = "ReportMutation", field = "acceptQuestionReport")
    public QuestionReportResponse acceptQuestionReport(
            @Argument String questionId,
            @Argument(name = "userId") String userId) {

        UUID userUUID = (userId != null) ? parseUUID(userId) : dummyUserId;
        log.info("GraphQL 질문 신고 수락 요청 - userId: {}, questionId: {}", userUUID, questionId);

        try {
            UUID questionUUID = parseUUID(questionId);

            AcceptQuestionReportRequest request = AcceptQuestionReportRequest.builder()
                    .userId(userUUID)
                    .questionId(questionUUID)
                    .build();

            return reportApplication.acceptQuestionReport(request);
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 질문 신고 수락 실패 - 잘못된 UUID 형식: userId: {}, questionId: {}, error: {}", 
                    userUUID, questionId, e.getMessage());
            
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (ResourceNotFoundException e) {
            log.warn("GraphQL 질문 신고 수락 실패 - 리소스 없음: userId: {}, questionId: {}, error: {}", 
                    userUUID, questionId, e.getMessage());
            
            return QuestionReportResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (BusinessException e) {
            log.warn("GraphQL 질문 신고 수락 실패 - 비즈니스 오류: userId: {}, questionId: {}, error: {}", 
                    userUUID, questionId, e.getMessage());
            
            return QuestionReportResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("GraphQL 질문 신고 수락 실패 - 예상치 못한 오류: userId: {}, questionId: {}, error: {}", 
                    userUUID, questionId, e.getMessage(), e);
            
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 수락 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 문자열 UUID를 UUID 객체로 파싱합니다.
     * 
     * @param uuidString UUID 문자열
     * @return 파싱된 UUID 객체
     * @throws IllegalArgumentException 잘못된 UUID 형식인 경우
     */
    private UUID parseUUID(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 UUID 형식: {}", uuidString);
            throw new IllegalArgumentException("잘못된 UUID 형식입니다: " + uuidString);
        }
    }
}