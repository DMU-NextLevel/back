package NextLevel.demo.project;

public enum ProjectStatus {
    PENDING, // 시작 전
    PROGRESS, // 진행 중
    STOPPED, // 중단 됨 by admin
    SUCCESS, // 성공 (만료 기간 넘어감)
    FAIL, // 실패 (만료 기간 넘어감)
    END // 종료
    // DELETED 삭제됨 soft delete
    ;

    public boolean isAvailable() {
        return this.equals(ProjectStatus.PROGRESS);
    }
}
