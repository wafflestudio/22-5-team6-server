import requests
import json

# Base URL of the API server - replace this with actual domain
BASE_URL = ""
EC2_URL = ""

# Sample login credentials
credentials = {
    "username": "testuser",
    "password": "testpassword"
}

# Sample room data
sample_rooms = [
    {
        "roomName": "Cozy Studio in Gangnam",
        "description": "Modern studio apartment with city view",
        "roomType": "APARTMENT",
        "address": {
            "sido": "서울특별시",
            "sigungu": "강남구",
            "street": "테헤란로 123",
            "detail": "1001호"
        },
        "roomDetails": {
            "wifi": True,
            "selfCheckin": True,
            "luggage": True,
            "tv": True,
            "bedroom": 1,
            "bathroom": 1,
            "bed": 1
        },
        "price": {
            "perNight": 80000,
            "cleaningFee": 20000,
            "charge": 5000,
            "total": 0
        },
        "maxOccupancy": 2,
        "imageSlot": 3
    },
    {
        "roomName": "Luxury Apartment in Hongdae",
        "description": "Spacious 2-bedroom apartment near Hongik University",
        "roomType": "APARTMENT",
        "address": {
            "sido": "서울특별시",
            "sigungu": "마포구",
            "street": "홍익로 456",
            "detail": "502호"
        },
        "roomDetails": {
            "wifi": True,
            "selfCheckin": True,
            "luggage": True,
            "tv": True,
            "bedroom": 2,
            "bathroom": 1,
            "bed": 2
        },
        "price": {
            "perNight": 120000,
            "cleaningFee": 30000,
            "charge": 8000,
            "total": 0
        },
        "maxOccupancy": 4,
        "imageSlot": 5
    }
]

def register_user(username, password, nickname, bio):
    """Register a new user"""
    register_url = f"{BASE_URL}/api/auth/register"
    
    registration_data = {
        "username": username,
        "password": password,
        "nickname": nickname,
        "bio": bio,
        "showMyReviews": True,
        "showMyReservations": True
    }
    
    headers = {
        'Content-Type': 'application/json'
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
                            headers={'Content-Type': 'image/jpeg'}
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
