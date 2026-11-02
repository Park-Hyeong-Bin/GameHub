# GameHub

GameHub는 게임 정보를 탐색하고, 리뷰를 남기며, 사용자별 취향을 관리할 수 있는 Android 앱입니다. Kotlin 기반으로 개발되었으며 Firebase를 활용해 로그인, 데이터 저장, 사용자별 활동 관리 등을 제공합니다.

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1.8%2B-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Firebase-Enabled-FFCA28?logo=firebase&logoColor=black" alt="Firebase" />
</p>

## 프로젝트 소개

GameHub는 사용자에게 게임 추천, 게임 상세 정보 확인, 리뷰 작성, 찜/좋아요/평점 관리 기능을 제공하는 모바일 앱입니다. 게임 커뮤니티형 UX를 중심으로 구성되어 있으며, Firebase 인증 및 데이터 저장 구조를 활용해 개인화된 경험을 제공합니다.

주요 기능으로는 다음이 포함됩니다.

- 회원가입 및 로그인
- 게임 탐색 및 홈 화면
- 게임 상세 정보 및 리뷰 확인
- 평점, 리뷰 작성, 댓글 기능
- 찜 목록, 보관함, 좋아요한 항목 관리
- 사용자 프로필 및 설정

## 기술 스택

- Kotlin
- Android Jetpack
- ViewBinding
- Firebase Authentication
- Firebase Firestore / Realtime Database
- Firebase Storage
- Firebase Analytics / Crashlytics
- Glide
- Material Components
- Navigation Component
- AndroidX

## 앱 구조

```text
GameHub/
├── app/
│   ├── src/
│   │   ├── androidTest/
│   │   ├── main/
│   │   └── test/
│   ├── build.gradle
│   ├── google-services.json
│   ├── proguard-rules.pro
│   └── .gitignore
├── gradle/
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
├── .gitignore
├── README.md
└── .idea/
```

## 주요 기능

### 1) 인증 시스템
- `LoginActivity`, `SignupActivity`를 통해 사용자 로그인 및 회원가입 기능 제공
- Firebase Authentication 연동 가능 구조

### 2) 게임 탐색
- `HomeFragment`, `FindFragment`, `GameFragment`에서 게임 목록, 추천 요소, 상세 정보 탐색
- `GameAdapter`, `HomeAdapter`, `TagAdapter`를 통해 UI 구성

### 3) 리뷰 및 커뮤니티 기능
- `ReviewFragment`, `ReviewAdapter`를 통해 게임 평점 및 리뷰 조회
- `CommentFragment`, `CommentDialogFragment`에서 댓글 작성 및 열람

### 4) 개인화 기능
- `MyPageFragment`, `MyFavoriteFragment`, `MyWishlistFragment`, `MyArchiveFragment` 등을 통해 사용자 활동 관리
- 평점, 찜, 보관함, 저장 목록 등을 개인 프로필에 반영

### 5) 설정 및 환경 설정
- `PreferenceFragment`를 통해 사용자 설정 및 옵션 관리

## 실행 전 준비 사항

다음 항목이 준비되어 있어야 프로젝트를 빌드할 수 있습니다.

- Android Studio
- JDK 8 이상
- Android SDK
- Android Emulator 또는 실제 기기
- Firebase 프로젝트 구성

## 설치 및 실행

1. 저장소를 클론합니다.

```bash
git clone https://github.com/Park-Hyeong-Bin/GameHub.git
cd GameHub
```

2. Android Studio에서 프로젝트를 엽니다.

3. Gradle 동기화를 수행합니다.

4. Firebase 설정을 확인합니다.
   - `app/google-services.json` 파일이 있어야 합니다.
   - Firebase 프로젝트의 패키지 이름과 앱의 패키지 이름이 일치하는지 확인합니다.

5. 에뮬레이터 또는 연결된 기기에서 앱을 실행합니다.

## 빌드

```bash
./gradlew build
```

Windows 환경에서는 다음을 실행합니다.

```bash
gradlew.bat build
```

## Firebase 설정 참고

이 프로젝트는 Firebase 기반 기능을 사용하도록 구성되어 있습니다. 실제로 빌드 및 실행하려면 다음을 확인해야 합니다.

- `app/google-services.json` 파일이 올바르게 포함되어 있는지
- Firebase 콘솔에서 앱 등록이 정상적으로 완료되었는지
- 사용 중인 패키지 이름과 Firebase의 앱 패키지 이름이 일치하는지

## 프로젝트 개발 참고

앱의 메인 소스 코드는 다음 경로에 위치합니다.

- `app/src/main/java/com/example/gamehub`

리소스 파일은 다음 경로에 있습니다.

- `app/src/main/res`

앱 실행 진입점은 다음 파일에서 확인할 수 있습니다.

- `app/src/main/AndroidManifest.xml`

## 기여자

- [Park Hyeong-Bin](https://github.com/Park-Hyeong-Bin)
- [sbjiwan](https://github.com/sbjiwan)
- [insu57](https://github.com/insu57)
- [agbrgh](https://github.com/agbrgh)

## 라이선스

현재 저장소에는 라이선스 파일이 명시적으로 포함되어 있지 않습니다. 공개 배포, 상업적 활용, 또는 다른 프로젝트에서의 재사용을 원한다면 라이선스 추가를 권장합니다.

## 참고

이 README는 프로젝트의 구조와 기능을 반영해 작성되었으며, 실제 실행 환경에 따라 Firebase 설정 값과 SDK 버전이 달라질 수 있습니다. 로컬 환경에서 빌드하거나 확장할 때는 `build.gradle` 및 Firebase 구성을 함께 확인하는 것을 권장합니다.
