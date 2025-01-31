import requests
import sys
import json
import random

# Base URL of the API server - replace this with actual domain
EC2_URL = ""

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

def getSampleRoom():
    loc = getLocation()
    typ = getType()
    adj = getAdjective()
    desc = getSimpleDescriptions()
    return {
        "roomName": f"{adj} {typ.lower().capitalize()} in {loc["sido"]}",
        "description": desc.replace("PLACEHOLDER", typ.lower()),
        "roomType": typ,
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
        "imageSlot": random.randint(3, 5)
    }

def register_user(username, password, nickname, bio):
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
            return True, image_upload_url
        else:
            print(f"Registration failed with status code: {response.status_code}, message: {response.content}")
            return False, None
            
    except Exception as e:
        print(f"Error during registration: {str(e)}")
        return False, None

def login_user(username, password):
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


def create_rooms():
    if len(sys.argv) != 2:
        print("Usage: python generate-rooms-api-post.py <number_of_rooms>")
        sys.exit(1)
    
    try:
        num_rooms = int(sys.argv[1])
    except ValueError:
        print("Please provide a valid number")
        sys.exit(1)

    username = "testuser"
    password = "testpass123"
    nickname = "Test User"
    bio = "Hello, I'm a test user"
    token = None
    
    # First register
    success, image_url = register_user(username, password, nickname, bio)
    
    # Then login
    success, token = login_user(username, password)
    if success:
        print(f"Authorization token: {token}")
    
    # Extract token from login response
    headers = {
        'Authorization': token,
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
                headers=headers,
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
                
            else:
                print(f"Failed to create room {room['roomName']}")
                print(f"Status code: {response.status_code}. Body: {response.content}")
                
        except Exception as e:
            print(f"Error creating room {room['roomName']}: {str(e)}")

if __name__ == "__main__":
    create_rooms()

