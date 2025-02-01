import os
import requests
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
from PIL import Image
import time
from io import BytesIO


def fetch_dynamic_html(url, scroll_pause_time, max_scrolls):
    """Selenium Manager로 동적 페이지 HTML 가져오기"""
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--no-sandbox")
    driver = webdriver.Chrome(options=chrome_options)
    driver.get(url)

    # 스크롤 반복
    scroll_count = 0
    while scroll_count < max_scrolls:
        driver.find_element(By.TAG_NAME, "body").send_keys(Keys.END)
        time.sleep(scroll_pause_time)
        scroll_count += 1

    # 최종 HTML 가져오기
    html = driver.page_source
    driver.quit()
    return html


def parse_images_from_dynamic_html(html, max_images, min_width=200, min_height=300):
    """최고의 전망 필터가 적용된 div에서 숙소 이미지 URL 필터링 (작은 이미지 제외)"""
    soup = BeautifulSoup(html, "html.parser")
    image_urls = []

    # "최고의 전망" 필터가 적용된 div 찾기
    filtered_divs = soup.find_all("div", class_="dmzfgqv atm_5sauks_glywfm dir dir-ltr")

    # # 해당 div 안에서만 img 태그 탐색
    # for div in filtered_divs:
    #     for img in div.find_all("img"):
    #         img_url = img.get("data-original-uri") or img.get("src")
    #         if img_url and "/im/pictures/" in img_url:  # 숙소 이미지 URL 필터링
    #             try:
    #                 response = requests.get(img_url)
    #                 if response.status_code == 200:
    #                     image = Image.open(BytesIO(response.content))
    #                     width, height = image.size
    #                     if width >= 100 and height >= 100:
    #                         image_urls.append(img_url)
    #                         if len(image_urls) >= max_images:  # 최대 이미지 수 제한
    #                             return image_urls
    #             except Exception as e:
    #                 print(f"Error processing image {img_url}: {e}")

    # return image_urls
    # 해당 div 안에서만 img 태그 탐색
    for div in filtered_divs:
        for img in div.find_all("img"):
            img_url = img.get("data-original-uri") or img.get("src")
            if img_url and "/im/pictures/" in img_url:  # 숙소 이미지 URL 필터링
                try:
                    response = requests.get(img_url)
                    if response.status_code == 200:
                        image = Image.open(BytesIO(response.content))
                        width, height = image.size
                        if width >= 100 and height >= 100:
                            image_urls.append(img_url)
                            if len(image_urls) >= max_images:  # 최대 이미지 수 제한
                                return image_urls
                except Exception as e:
                    print(f"Error processing image {img_url}: {e}")

    return image_urls


def get_next_image_index(save_dir):
    """다음 저장할 이미지 인덱스를 계산"""
    if not os.path.exists(save_dir):
        return 0  # 디렉토리가 없으면 0부터 시작
    existing_files = [f for f in os.listdir(save_dir) if f.startswith("image_") and f.endswith(".jpg")]
    if not existing_files:
        return 0  # 이미지 파일이 없으면 0부터 시작
    # 파일 이름에서 숫자 추출 후 가장 큰 숫자 + 1 반환
    indices = [int(f.split('_')[1].split('.')[0]) for f in existing_files]
    return max(indices) + 1


def save_images(image_urls, save_dir='images'):
    """이미지 저장"""
    if not os.path.exists(save_dir):
        os.makedirs(save_dir)

    start_index = get_next_image_index(save_dir)  # 저장 시작 인덱스 계산
    image_paths = []
    for i, url in enumerate(image_urls):
        try:
            image_index = start_index + i  # 현재 인덱스
            image_path = os.path.join(save_dir, f'image_{image_index}.jpg')
            image_data = requests.get(url).content
            with open(image_path, 'wb') as handler:
                handler.write(image_data)
            image_paths.append(image_path)
        except Exception as e:
            print(f"Failed to download image: {url} - {e}")

    return image_paths


if __name__ == "__main__":
    # Airbnb URL (최고의 전망 필터가 적용된 페이지)
    airbnb_url = 'https://www.airbnb.com/s/Tokyo/homes?sort_order=review_scores'  # Replace with actual Airbnb listing URL
    # https://www.airbnb.com/s/Switzerland/homes
    # https://www.airbnb.com/s/United-States/homes
    # https://www.airbnb.com/s/Paris/homes
    # https://www.airbnb.com/s/Seoul/homes
    # https://www.airbnb.com/s/Tokyo/homes
    # https://www.airbnb.com/s/Beijing--China/homes
    # https://www.airbnb.com/s/Bangkok--Thailand/homes
    # https://www.airbnb.com/s/Sydney--Australia/homes
    # https://www.airbnb.com/s/Rio-de-Janeiro--Brazil/homes
    # https://www.airbnb.com/s/Cape-Town--South-Africa/homes
    # Selenium으로 동적 HTML 가져오기
    print("동적 페이지 로딩 중...")
    dynamic_html = fetch_dynamic_html(airbnb_url, scroll_pause_time=0.5, max_scrolls=10)

    # 이미지 URL 파싱 (최대 50개, 최소 크기: 200x300)
    print("이미지 URL 파싱 중...")
    image_urls = parse_images_from_dynamic_html(dynamic_html, max_images=8, min_width=200, min_height=300)
    print(f"총 {len(image_urls)}개의 이미지를 추출했습니다.")

    # 이미지 저장
    if image_urls:
        print("이미지 저장 중...")
        image_paths = save_images(image_urls)
        print(f"이미지가 저장되었습니다: {image_paths}")
    else:
        print("이미지를 찾을 수 없습니다.")
