import requests
import sys
import json
import random
import os
from datetime import datetime, timedelta

# Base URL of the API server - replace this with actual domain
EC2_URL = "http://ec2-15-165-159-152.ap-northeast-2.compute.amazonaws.com:8080/"

# Sample login credentials
credentials = {
    "username": "testuser",
    "password": "testpassword"
}

import random

def getLocation():
    korea_locations = {
        "서울특별시": ["강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", 
                  "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구",
                  "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"],
        "부산광역시": ["강서구", "금정구", "남구", "동구", "동래구", "부산진구", "북구", "사상구",
                  "사하구", "서구", "수영구", "연제구", "영도구", "중구", "해운대구", "기장군"],
        "대구광역시": ["남구", "달서구", "동구", "북구", "서구", "수성구", "중구", "달성군"],
        "인천광역시": ["계양구", "남동구", "동구", "미추홀구", "부평구", "서구", "연수구", "중구", "강화군", "옹진군"],
        "광주광역시": ["광산구", "남구", "동구", "북구", "서구"],
        "대전광역시": ["대덕구", "동구", "서구", "유성구", "중구"],
        "울산광역시": ["남구", "동구", "북구", "중구", "울주군"],
        "세종특별자치시": ["세종시"],
        "경기도": ["수원시", "성남시", "의정부시", "안양시", "부천시", "광명시", "평택시", "동두천시", "안산시", 
                "고양시", "과천시", "구리시", "남양주시", "오산시", "시흥시", "군포시", "의왕시", "하남시",
                "용인시", "파주시", "이천시", "안성시", "김포시", "화성시", "광주시", "양주시", "포천시",
                "여주시", "연천군", "가평군", "양평군"],
        "강원도": ["춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시",
                "홍천군", "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군",
                "양구군", "인제군", "고성군", "양양군"],
        "충청북도": ["청주시", "충주시", "제천시", "보은군", "옥천군", "영동군", "증평군",
                 "진천군", "괴산군", "음성군", "단양군"],
        "충청남도": ["천안시", "공주시", "보령시", "아산시", "서산시", "논산시", "계룡시",
                 "당진시", "금산군", "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군"],
        "전라북도": ["전주시", "군산시", "익산시", "정읍시", "남원시", "김제시",
                 "완주군", "진안군", "무주군", "장수군", "임실군", "순창군", "고창군", "부안군"],
        "전라남도": ["목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군",
                 "구례군", "고흥군", "보성군", "화순군", "장흥군", "강진군", "해남군",
                 "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군"],
        "경상북도": ["포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시",
                 "상주시", "문경시", "경산시", "군위군", "의성군", "청송군", "영양군",
                 "영덕군", "청도군", "고령군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"],
        "경상남도": ["창원시", "진주시", "통영시", "사천시", "김해시", "밀양시", "거제시",
                 "양산시", "의령군", "함안군", "창녕군", "고성군", "남해군", "하동군",
                 "산청군", "함양군", "거창군", "합천군"],
        "제주특별자치도": ["제주시", "서귀포시"]
    }
    
    sido = random.choice(list(korea_locations.keys()))
    sigungu = random.choice(korea_locations[sido])
    
    return {
        "sido": sido,
        "sigungu": sigungu
    }

def getType():
    types = ["APARTMENT", "HOUSE", "VILLA", "HANOK", "SWIMMING_POOL", "HOTEL", "CAMPING", "FARM", "ISLAND"]
    return random.choice(types)

def getAdjective():
    adjs = ["Cozy", "Comfortable", "Spacious", "Luxurious", "Modern", "Rustic", "Stylish", "Charming", "Elegant", "Sunny"]
    return random.choice(adjs)

def getSimpleDescriptions():
    descs = ["Great PLACEHOLDER to stay", "Cozy and comfortable PLACEHOLDER", "PLACEHOLDER perfect for a weekend getaway", "PLACEHOLDER with amazing view", "PLACEHOLDER close to the city center"]
    return random.choice(descs)

def getSampleRoom(room_type):
    loc = getLocation()
    adj = getAdjective()
    desc = getSimpleDescriptions()
    return {
        "roomName": f"{adj} {room_type.lower().capitalize()} in {loc["sido"]}",
        "description": desc.replace("PLACEHOLDER", room_type.lower()),
        "roomType": room_type, # 입력받게 변경
        "address": {
            "sido": loc["sido"],
            "sigungu": loc["sigungu"],
            "street": f"공원로 {random.randint(0, 500)}",
            "detail": f"100{random.randint(0, 10)}호"
        },
        "roomDetails": {
            "wifi": True if random.randint(0, 1) == 0 else False,
            "selfCheckin": True if random.randint(0, 1) == 0 else False,
            "luggage": True if random.randint(0, 1) == 0 else False,
            "tv": True if random.randint(0, 1) == 0 else False,
            "bedroom": random.randint(1, 5),
            "bathroom": random.randint(1, 3),
            "bed": random.randint(1, 5)
        },
        "price": {
            "perNight": random.randint(50000, 80000),
            "cleaningFee": random.randint(10000, 30000),
            "charge": random.randint(2000, 10000),
            "total": 0
        },
        "maxOccupancy": random.randint(2, 8),
        "imageSlot": 5 # 5개로 고정 시킴
    }

def getSampleReview():
    # Generate a random review from sample reviews list
    reviews = [
        {"content": "최고의 숙박이었어요! 깔끔하고 설명된 그대로였습니다.", "rating": 5},
        {"content": "위치도 좋고 숙소도 매우 편안했어요.", "rating": 4},
        {"content": "호스트님이 친절하시고 체류 기간 내내 빠른 응답해주셨어요.", "rating": 5},
        {"content": "완벽한 휴가 장소네요! 다음에 또 올게요.", "rating": 5},
        {"content": "깨끗하고 아늑하며 필요한 모든 것이 갖춰져 있었어요.", "rating": 4},
        {"content": "아름다운 숙소에 멋진 전망까지! 강력 추천합니다!", "rating": 5},
        {"content": "기대 이상으로 좋았습니다.", "rating": 4},
        {"content": "편의시설도 훌륭하고 침대도 매우 편안했어요.", "rating": 4}, 
        {"content": "가격 대비 훌륭한 숙소였습니다. 다음에 또 이용할게요!", "rating": 4},
        {"content": "정말 평화롭고 편안한 환경이었습니다.", "rating": 5},
        {"content": "시설이 많이 낡았어요. 기대했던 것보다 실망스러웠네요.", "rating": 2},
        {"content": "청소상태가 좋지 않았고 냄새가 났어요.", "rating": 1},
        {"content": "가격에 비해 서비스가 많이 부족했습니다.", "rating": 2},
        {"content": "호스트와 연락이 잘 되지 않아 불편했어요.", "rating": 2},
        {"content": "주변이 너무 시끄럽고 잠을 잘 수 없었어요.", "rating": 1},
        {"content": "욕실에 문제가 있었는데 해결해주지 않았어요.", "rating": 2},
        {"content": "사진과 실제 숙소가 많이 달랐습니다.", "rating": 1},
        {"content": "난방이 제대로 되지 않아 추웠어요.", "rating": 2}
    ]
    return random.choice(reviews)

def getSampleName():
    names = ["김철수", "이영희", "박민수", "정미영", "홍길동", "최영수", "이순신", "유재석", "박명수", "강호동"]
    return random.choice(names)

def getSampleBio():
    bios = [
        "안녕하세요! 여행을 사랑하는 직장인입니다.",
        "깔끔한 숙소를 운영하려 노력하고 있습니다.",
        "좋은 추억 만드실 수 있도록 도와드리겠습니다.",
        "반갑습니다! 여행 초보자입니다.",
        "편안한 휴식 제공을 목표로 합니다.",
        "여행하면서 만난 인연을 소중히 여깁니다.",
        "차 한잔의 여유를 즐기는 호스트입니다.",
        "맛집 탐방이 취미인 여행자입니다.",
        "아늑한 공간을 만드는 것이 즐거워요.",
        "여행의 즐거움을 나누고 싶습니다."
        "여행을 통해 새로운 경험을 추구합니다.",
        "맛있는 음식과 여행이 삶의 낙입니다.",
        "여러분의 여행을 더욱 특별하게 만들어드립니다.",
        "여행지의 로컬 문화를 사랑합니다.",
        "게스트님들의 편안함을 최우선으로 생각합니다.",
        "여행의 설렘을 함께 나누고 싶어요.",
        "친환경적인 라이프스타일을 추구합니다.",
        "여행으로 삶의 여유를 찾습니다.",
        "문화와 예술을 사랑하는 호스트입니다.",
        "소소한 일상의 행복을 나누고 싶어요."
    ]
    return random.choice(bios)

def register_user(username, password, nickname, bio, **kwargs):
    """Register a new user"""
    register_url = f"{EC2_URL}/api/auth/register"
    
    registration_data = {
        "username": username,
        "password": password,
        "nickname": nickname,
        "bio": bio,
        "showMyReviews": True,
        "showMyReservations": True,
        "showMyWishlist": True
    }
    
    headers = {
        'Content-Type': 'application/json',
    }
    
    try:
        response = requests.post(
            register_url,
            data=json.dumps(registration_data),
            headers=headers
        )
        
        if response.status_code == 200:
            print("Registration successful!")
            # Get the image upload URL from response
            image_upload_url = response.json().get('imageUploadUrl')
            # Upload image
            requests.put(
                image_upload_url,
                data=get_picsum_image(),
                headers={'Content-Type': 'image/jpeg', 'Cache-Control': 'no-cache, no-store, must-revalidate'}
            )
            return True
        else:
            print(f"Registration failed with status code: {response.status_code}, message: {response.content}")
            return False, None
            
    except Exception as e:
        print(f"Error during registration: {str(e)}")
        return False, None

def login_user(username, password, **kwargs):
    """Login with registered credentials"""
    login_url = f"{EC2_URL}/api/auth/login"
    
    login_data = {
        "username": username,
        "password": password
    }
    
    headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
    
    try:
        response = requests.post(
            login_url,
            data=login_data,
            headers=headers
        )
        
        if response.status_code == 200:
            print("Login successful!")
            # Get authorization token from header
            auth_token = "Bearer " + json.loads(response.content)["token"]
            return True, auth_token
        else:
            print(f"Login failed with status code: {response.status_code}")
            return False, None
            
    except Exception as e:
        print(f"Error during login: {str(e)}")
        return False, None

def get_picsum_image(width=1200, height=800):
    """Get a random image from Lorem Picsum"""
    response = requests.get(f"https://picsum.photos/{width}/{height}", allow_redirects=True)
    return response.content

# def get_availabile_dates(room_id, year, month):
#     try:
#         response = requests.get(
#             f"{EC2_URL}/api/v1/reservations/availability/{room_id}",
#             params={"year": year, "month": month}
#         )
#         return response.json()['availableDates'] if response.status_code == 200 else None
#     except Exception as e:
#         print(f"Error checking availability: {str(e)}")
#         return None

def get_availabile_dates(room_id, year, month):
    try:
        response = requests.get(
            f"{EC2_URL}/api/v1/reservations/availability/{room_id}",
            params={"year": year, "month": month}
        )
        if response.status_code == 200:
            return response.json().get('availableDates', [])
        else:
            print(f"Error checking availability: Status code {response.status_code}")
            return []
    except Exception as e:
        print(f"Error checking availability: {str(e)}")
        return []

def create_reservation(room_id, auth_token, dt):
    headers = {
        "Authorization": f"{auth_token}",
        "Content-Type": "application/json"
    }
    
    d = random.randint(-5, 5)

    # check availability
    availabe_dates = get_availabile_dates(room_id, dt.year, dt.month)
    if not available_dates:
        print(f"No available dates for room {room_id} in {dt.year}-{dt.month}")
        return None

    start_date = random.choice(availabe_dates)
    end_date = (datetime.strptime(start_date, "%Y-%m-%d") + timedelta(days=1)).strftime("%Y-%m-%d")
    
    reservation_data = {
        "roomId": room_id,
        "startDate": start_date,
        "endDate": end_date,
        "numberOfGuests": 2
    }
    
    try:
        response = requests.post(
            f"{EC2_URL}/api/v1/reservations",
            headers=headers,
            json=reservation_data
        )
        #print(response.status_code, response.json())
        # print reservation info
        if response.status_code == 201:
            print(f"Created reservation for room {room_id} from {start_date} to {end_date}")
        return response.json()["reservationId"] if response.status_code == 201 else None
    except Exception as e:
        print(f"Error creating reservation: {str(e)}")
        return None


def create_past_reservation(room_id, auth_token, start_date, end_date):
    headers = {
        "Authorization": f"{auth_token}",
        "Content-Type": "application/json"
    }

    reservation_data = {
        "roomId": room_id,
        "startDate": start_date,
        "endDate": end_date,
        "numberOfGuests": 2
    }

    try:
        response = requests.post(
            f"{EC2_URL}/api/v1/reservations",
            headers=headers,
            json=reservation_data
        )
        if response.status_code == 201:
            print(f"Created reservation for room {room_id} from {start_date} to {end_date}")
            return response.json()["reservationId"]
        else:
            print(f"Failed to create reservation. Status code: {response.status_code}")
            return None
    except Exception as e:
        print(f"Error creating reservation: {str(e)}")
        return None

def create_review(reservation_id, auth_token):
    headers = {
        "Authorization": f"{auth_token}",
        "Content-Type": "application/json"
    }
    
    review_data = {
        "reservationId": reservation_id,
        **getSampleReview()
    }
    
    try:
        response = requests.post(
            f"{EC2_URL}/api/v1/reviews",
            headers=headers,
            json=review_data
        )
        return response.json()["reviewId"] if response.status_code == 201 else None
    except Exception as e:
        print(f"Error creating review: {str(e)}")
        return None

def create_rooms():
    if len(sys.argv) != 2:
        print("Usage: python generate-rooms-api-post.py <number_of_rooms>")
        sys.exit(1)
    
    try:
        num_rooms = int(sys.argv[1])
    except ValueError:
        print("Please provide a valid number")
        sys.exit(1)

    hostuser = {
        "username": "testuser1",
        "password" : "testpass123",
        "nickname" : "Test User",
        "bio" : "Hello, I'm a test user",
        "token" : None
    }
    
    testusers = [{
        "username": f"guestuser{i}",
        "password" : f"testpass{i}",
        "nickname" : getSampleName(),
        "bio" : getSampleBio(),
        "token" : None
    } for i in range(10)]
    
    # First register
    success = register_user(**hostuser)
    for user in testusers:
        success = register_user(**user)
    
    # Then login
    success, token = login_user(**hostuser)
    if success:
        hostuser['token'] = token
        print(f"Authorization token: {token}")

    for user in testusers:
        success, token = login_user(**user)
        if success:
            user['token'] = token
            print(f"Authorization token: {token}")
    
    # Extract token from login response
    hostheaders = {
        'Authorization': hostuser['token'],
        'Content-Type': 'application/json'
    }

    sample_rooms = [getSampleRoom() for _ in range(num_rooms)]
    
    # Create each room
    for room in sample_rooms:
        try:
            # Create room
            create_room_url = f"{EC2_URL}/api/v1/rooms"
            response = requests.post(
                create_room_url,
                headers=hostheaders,
                json=room
            )
            
            if response.status_code == 201:
                room_data = response.json()
                print(f"Successfully created room: {room['roomName']}")
                print(f"Room ID: {room_data['roomId']}")
                print("Uploading images...")
                
                # Upload images using Lorem Picsum
                for i, upload_url in enumerate(room_data['imageUploadUrlList']):
                    try:
                        # Get random image from Lorem Picsum
                        image_data = get_picsum_image()
                        
                        # Upload to the provided URL
                        upload_response = requests.put(
                            upload_url,
                            data=image_data,
                            headers={'Content-Type': 'image/jpeg', 'Cache-Control': 'no-cache, no-store, must-revalidate'}
                        )
                        
                        if upload_response.status_code == 200:
                            print(f"Successfully uploaded image {i+1}")
                        else:
                            print(f"Failed to upload image {i+1}. Status code: {upload_response.status_code}")
                            
                    except Exception as e:
                        print(f"Error uploading image {i+1}: {str(e)}")
                print()

                # create reservation and review
                selected_users = random.sample(testusers, random.randint(4, 8))

                room_id = response.json()["roomId"]
                for user in selected_users:
                    # Create reservation
                    reservation_id = create_reservation(room_id, user['token'], datetime.now() + timedelta(days=30))
                    reservation_id = create_reservation(room_id, user['token'], datetime.now() + timedelta(days=-30))
                    if reservation_id:
                        print(f"Created reservation {reservation_id} for room {room_id}")
                        
                        # Create review
                        review_id = create_review(reservation_id, user['token'])
                        if review_id:
                            print(f"Created review {review_id} for reservation {reservation_id}")
                            
                        else:
                            print(f"Failed to create review for room {room['roomName']}")
                    else:
                        print(f"Failed to create reservation for room {room['roomName']}")

        except Exception as e:
            print(f"Error creating room {room['roomName']}: {str(e)}")

# Test Code 1 : 숙소 48개 등록 (이미지 240개) / 각각 예약 5개, 리뷰는 과거에 해당하는 것만
def test1():
    room_types = ["APARTMENT", "HOUSE", "VILLA", "HANOK", "SWIMMING_POOL", "HOTEL", "CAMPING", "FARM", "COUNTRY_SIDE", "RIVER_SIDE", "ISLAND", "SKI"]
    num_rooms_per_type = 4
    total_rooms = num_rooms_per_type * len(room_types)

    hostuser = {
        "username": "testuser2",
        "password": "testpass1234",
        "nickname": "Test User",
        "bio": "Hello, I'm a test user",
        "token": None
    }

    testusers = [{
        "username": f"guestuser{i}",
        "password": f"testpass{i}",
        "nickname": getSampleName(),
        "bio": getSampleBio(),
        "token": None
    } for i in range(10)]

    # Register and login users
    register_user(**hostuser)
    for user in testusers:
        register_user(**user)

    success, token = login_user(**hostuser)
    if success:
        hostuser['token'] = token

    for user in testusers:
        success, token = login_user(**user)
        if success:
            user['token'] = token

    hostheaders = {
        'Authorization': hostuser['token'],
        'Content-Type': 'application/json'
    }

    image_files = [f"images/{img}" for img in os.listdir("images") if img.endswith(".jpg")]
    image_index = 0

    for room_type in room_types:
        for _ in range(num_rooms_per_type):
            # room_type에 따라 다른 room 생성
            room = getSampleRoom(room_type)
            try:
                create_room_url = f"{EC2_URL}/api/v1/rooms"
                response = requests.post(
                    create_room_url,
                    headers=hostheaders,
                    json=room
                )

                if response.status_code == 201:
                    room_data = response.json()
                    print(f"Successfully created room: {room['roomName']}")
                    print(f"Room ID: {room_data['roomId']}")
                    print("Uploading images...")

                    # 이미지 업로드 - images 폴더내에 있는 파일들로
                    for i in range(5):
                        try:
                            image_path = image_files[image_index % len(image_files)]
                            with open(image_path, 'rb') as img_file:
                                image_data = img_file.read()

                            upload_url = room_data['imageUploadUrlList'][i]
                            upload_response = requests.put(
                                upload_url,
                                data=image_data,
                                headers={'Content-Type': 'image/jpeg', 'Cache-Control': 'no-cache, no-store, must-revalidate'}
                            )

                            if upload_response.status_code == 200:
                                print(f"Successfully uploaded image {i + 1}")
                            else:
                                print(f"Failed to upload image {i + 1}. Status code: {upload_response.status_code}")

                            image_index += 1

                        except Exception as e:
                            print(f"Error uploading image {i + 1}: {str(e)}")
                    print()

                    selected_users = random.sample(testusers, 6)
                    room_id = room_data["roomId"]
                    # 유저 6명이 각각 예약 6개씩 생성함. (과거 예약 3개, 미래 예약 3개)
                    for user in selected_users:
                        for _ in range(3):
                            create_reservation(room_id, user['token'], datetime.now() + timedelta(days=random.randint(1, 90)))
                            create_reservation(room_id, user['token'], datetime.now() - timedelta(days=random.randint(1, 90)))

                        # 변수명은 과거 예약이지만, 가능한 날짜에 대해서 그냥 예약 만드는거임
                        past_reservations = get_availabile_dates(room_id, datetime.now().year, datetime.now().month - 1)
                        for date in past_reservations:
                            create_past_reservation(room_id, user['token'], date, (datetime.strptime(date, "%Y-%m-%d") + timedelta(days=1)).strftime("%Y-%m-%d"))

                        for reservation in past_reservations:
                            create_review(reservation, user['token'])

            except Exception as e:
                print(f"Error creating room {room['roomName']}: {str(e)}")


if __name__ == "__main__":
    #create_rooms()
    test1()


