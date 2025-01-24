


# 22-5-team6-server

## 📌 프로젝트 소개
Airbnb 클론 코딩을 주제로 진행하였습니다.  
아래 프로젝트 배포 페이지와 함께 보시면 좋습니다.  
[프로젝트 바로가기](https://d2gjarpl85ijp5.cloudfront.net/)

---

## 🖥️ 주요 기능
### 회원가입 / 로그인 / 소셜 로그인
- **패키지**: `Config`, `user`
- 일반 폼 기반 회원가입 및 소셜 로그인(구글, 카카오, 네이버) 기능 구현.

### 유저 프로필 페이지
- **패키지**: `profile`, `Reservation`, `Review`, `User Service`
- **구현 내용**:
    - 유저 프로필에서 **위시리스트(좋아요한 방)**, **예약 내역**, **리뷰 내역** 조회.
    - 프로필 공개/비공개 설정 가능.
    - 상세 구현 함수: `getReservationsByUser`, `getReviewsByUser`, `getLikedRooms`.

### 메인 페이지 (숙소 조회 / 등록 / 검색)
- **패키지**: `room`
- **숙소 등록**:
    - 호스트가 되어 숙소 등록 가능.
    - 등록 항목: 이름, 설명, 타입(아파트, 빌라 등), 디테일(wifi, 체크인 등), 최대 수용인원, 가격(수수료, 청소비, 1박당 가격), 이미지.
    - **이미지 업로드**:
        - 클라이언트에서 파일 등록 후 **Presigned URL 발급**.
        - FE에서 S3에 이미지 업로드.
- **숙소 조회 및 검색**:
    - CloudFront 도메인을 통한 이미지 조회.
    - 페이지네이션된 Room 제공.
    - 검색 필터: 숙소 유형, 주소, 가격, 예약 가능 기간, 별점.
    - 상세 구현 함수: `getRooms`, `searchRoom`.
- **숙소 좋아요 기능**:
    - **Entity**: `RoomLikeEntity`.
    - 멱등성과 동시성 고려 (Pessimistic Lock, unique index 사용).

### 숙소 상세 페이지
- **패키지**: `Room`, `Reservation`, `Review`
- **구현 내용**:
    - 숙소 정보 제공: `RoomService`의 `getRoomDetails`.
    - 예약 관리 및 가능 여부 조회: `ReservationService`.
    - 리뷰 생성/수정/삭제 및 조회:
        - 프로필에서 작성한 리뷰 조회 가능: `getReviewByUser`.

### 이미지 관리
- **이미지 업로드**:
    - User 및 Room 이미지에 대해 Presigned URL 제공.
    - API 요청 시 Presigned URL을 포함하여 이미지 업로드 설계.
- **이미지 조회**:
    - Presigned URL 대신 CloudFront 도메인 주소 사용.

---

## 🧪 테스트
- 동시성 및 정합성 문제를 고려한 테스트 설계.
- 각 엔티티별 서비스 로직의 작동 여부를 확인하기 위한 단위 테스트 구현.




