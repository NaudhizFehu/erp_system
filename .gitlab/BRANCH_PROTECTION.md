# GitLab 브랜치 보호 규칙 설정 가이드

이 문서는 GitLab 저장소의 브랜치 보호 규칙을 설정하는 방법을 설명합니다.

## 🛡️ 브랜치 보호 규칙 설정

### 1. GitLab 저장소 설정 접근
1. GitLab 저장소 페이지에서 **Settings** → **Repository** 클릭
2. **Push Rules** 섹션에서 브랜치 보호 규칙 설정
3. **Merge Requests** 섹션에서 MR 설정

### 2. main 브랜치 보호 규칙

#### Push Rules 설정
- [x] **Deny deleting a tag**
- [x] **Deny deleting a branch**
- [x] **Require commit to be signed with GPG**
- [x] **Restrict pushes to this project**
- [x] **Restrict pushes to certain users**
- [x] **Restrict pushes to certain groups**

#### Merge Request 설정
- [x] **Require approval from code owners**
- [x] **Require approval from merge request author**
- [x] **Require approval from merge request reviewers**
- [x] **Require approval from merge request assignees**
- [x] **Require approval from merge request approvers**
- [x] **Require approval from merge request reviewers**
- [x] **Require approval from merge request assignees**
- [x] **Require approval from merge request approvers**

#### Pipeline 설정
- [x] **Require pipeline to pass before merge**
- [x] **Require all jobs to pass before merge**
- [x] **Require all jobs to pass before merge**
- [x] **Require all jobs to pass before merge**

### 3. develop 브랜치 보호 규칙

#### Push Rules 설정
- [x] **Deny deleting a tag**
- [x] **Deny deleting a branch**
- [x] **Require commit to be signed with GPG**
- [x] **Restrict pushes to this project**
- [x] **Restrict pushes to certain users**
- [x] **Restrict pushes to certain groups**

#### Merge Request 설정
- [x] **Require approval from code owners**
- [x] **Require approval from merge request author**
- [x] **Require approval from merge request reviewers**
- [x] **Require approval from merge request assignees**
- [x] **Require approval from merge request approvers**

#### Pipeline 설정
- [x] **Require pipeline to pass before merge**
- [x] **Require all jobs to pass before merge**

### 4. feature/* 브랜치 보호 규칙

#### Push Rules 설정
- [x] **Deny deleting a tag**
- [x] **Deny deleting a branch**
- [x] **Require commit to be signed with GPG**
- [x] **Restrict pushes to this project**
- [x] **Restrict pushes to certain users**
- [x] **Restrict pushes to certain groups**

#### Merge Request 설정
- [x] **Require approval from code owners**
- [x] **Require approval from merge request author**
- [x] **Require approval from merge request reviewers**
- [x] **Require approval from merge request assignees**
- [x] **Require approval from merge request approvers**

#### Pipeline 설정
- [x] **Require pipeline to pass before merge**
- [x] **Require all jobs to pass before merge**

## 🔧 추가 설정

### 1. CODEOWNERS 파일
`.gitlab/CODEOWNERS` 파일을 통해 코드 소유자를 지정합니다.

### 2. MR 템플릿
`.gitlab/merge_request_templates/` 디렉토리에 MR 템플릿을 설정합니다.

### 3. 이슈 템플릿
`.gitlab/issue_templates/` 디렉토리에 이슈 템플릿을 생성합니다.

### 4. 파이프라인 권한
**Settings > CI/CD > General**에서 파이프라인 권한을 설정합니다:
- [x] Enable CI/CD
- [x] Enable Auto DevOps
- [x] Enable Container Registry
- [x] Enable GitLab Pages

## 📋 체크리스트

### 저장소 설정
- [ ] 브랜치 보호 규칙 설정 완료
- [ ] CODEOWNERS 파일 설정 완료
- [ ] MR 템플릿 설정 완료
- [ ] 이슈 템플릿 설정 완료
- [ ] 파이프라인 권한 설정 완료

### 팀 설정
- [ ] 팀 멤버 초대 완료
- [ ] 팀 권한 설정 완료
- [ ] 코드 리뷰어 지정 완료

### CI/CD 설정
- [ ] GitLab CI/CD 파이프라인 설정 완료
- [ ] 상태 체크 설정 완료
- [ ] 아티팩트 업로드 설정 완료

## 🚨 주의사항

1. **관리자 권한**: 브랜치 보호 규칙은 저장소 관리자만 설정할 수 있습니다.
2. **상태 체크**: CI/CD 파이프라인이 설정되어 있어야 상태 체크가 작동합니다.
3. **코드 소유자**: CODEOWNERS 파일에 지정된 사용자만 코드 리뷰를 할 수 있습니다.
4. **선형 히스토리**: main 브랜치에서는 선형 히스토리를 강제하여 깔끔한 커밋 히스토리를 유지합니다.

## 🔄 업데이트

브랜치 보호 규칙은 필요에 따라 업데이트할 수 있습니다. 변경사항이 있을 때마다 이 문서를 업데이트해주세요.

