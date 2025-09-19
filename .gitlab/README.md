# GitLab 설정 파일들

이 디렉토리는 GitLab 저장소의 설정 파일들을 포함합니다.

## 📁 파일 구조

```
.gitlab/
├── CODEOWNERS                    # 코드 소유자 설정
├── BRANCH_PROTECTION.md          # 브랜치 보호 규칙 설정 가이드
├── merge_request_templates/      # MR 템플릿
│   └── default.md
├── issue_templates/              # 이슈 템플릿
│   ├── bug_report.md
│   ├── feature_request.md
│   ├── security_report.md
│   ├── performance_issue.md
│   ├── documentation_issue.md
│   └── refactoring_issue.md
└── README.md                     # 이 파일
```

## 🔧 설정 방법

### 1. CODEOWNERS 설정
- `.gitlab/CODEOWNERS` 파일을 통해 코드 소유자를 지정
- GitLab 저장소 설정에서 "Require approval from code owners" 활성화

### 2. MR 템플릿 설정
- `.gitlab/merge_request_templates/` 디렉토리의 템플릿 사용
- GitLab에서 자동으로 인식하여 MR 생성 시 템플릿 적용

### 3. 이슈 템플릿 설정
- `.gitlab/issue_templates/` 디렉토리의 템플릿 사용
- GitLab에서 자동으로 인식하여 이슈 생성 시 템플릿 적용

### 4. 브랜치 보호 규칙 설정
- `.gitlab/BRANCH_PROTECTION.md` 파일의 가이드 참조
- GitLab 저장소 설정에서 Push Rules 및 Merge Request 설정

## 📋 체크리스트

### 저장소 설정
- [ ] CODEOWNERS 파일 설정 완료
- [ ] MR 템플릿 설정 완료
- [ ] 이슈 템플릿 설정 완료
- [ ] 브랜치 보호 규칙 설정 완료

### 팀 설정
- [ ] 팀 멤버 초대 완료
- [ ] 팀 권한 설정 완료
- [ ] 코드 리뷰어 지정 완료

### CI/CD 설정
- [ ] GitLab CI/CD 파이프라인 설정 완료
- [ ] 상태 체크 설정 완료
- [ ] 아티팩트 업로드 설정 완료

## 🚨 주의사항

1. **관리자 권한**: 대부분의 설정은 저장소 관리자만 변경할 수 있습니다.
2. **템플릿 파일**: 템플릿 파일은 GitLab에서 자동으로 인식하므로 파일명과 위치를 정확히 유지해야 합니다.
3. **권한 설정**: 팀 멤버의 권한을 적절히 설정하여 보안을 유지해야 합니다.

## 🔄 업데이트

설정 파일들은 필요에 따라 업데이트할 수 있습니다. 변경사항이 있을 때마다 관련 문서를 업데이트해주세요.

