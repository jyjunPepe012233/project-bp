# 0. 테스트 시 참고

### 0-1. 컨테이너 빌드 시 아래 명령어 사용 바람
- `.env.test` 파일을 환경변수 파일로 적용하여 빌드
``` 
docker compose --env-file .env.test up --build
```

### 0-2. 컨테이너 실행 후 UI 접속

- `localhost:3000`으로 UI 접근 가능
- ※주의※ AI로 구현하여서 오류가 발생할 수 있음. **사용자께서는 번거로우시겠지만 새로고침 등으로 대응 바랍니다.**

### 0-3. 테스트 데이터 사용

- "0-1"번 절차를 따랐다면, 첫 컨테이너 실행 후 테스트 데이터가 생성됨
- 테스트 계정 ID: `seed_root`
- 테스트 계정 PW: `seed_root_1234`


# 1. 프로젝트 소개
<img width="2940" height="1408" alt="image" src="https://github.com/user-attachments/assets/ffecc6e8-f6e0-4ffc-95f0-1fccf7d0b289" />

- Unity Addressables 기반 라이브 패치를 지원하는 플랫폼입니다.
- API나 공식 콘솔 UI를 통해 서비스를 사용하실 수 있습니다.
