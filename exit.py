import time
import cv2
import requests
import tensorflow as tf
import numpy as np
from ultralytics import YOLO
from gpiozero import DistanceSensor, Servo
from gpiozero.pins.pigpio import PiGPIOFactory
from config import idparkir, model, modelyolo, class_names, api

#pin
echo_pin = 24
trigger_pin = 23
servo_pin = 12

#setup sensor
factory = PiGPIOFactory()
ultrasonic = DistanceSensor(echo=echo_pin, trigger=trigger_pin)
servo = Servo(servo_pin, pin_factory=factory)
    
#buka gerbang
def open_gate():
    while True:
        distance = ultrasonic.distance * 100
        print(f"Distance: {distance:.2f} cm")
        
        if distance > 10:
            servo.min()
            print("Open Gate")
        elif distance <= 10:
            while distance <= 10:  
                distance = ultrasonic.distance * 100
                print(f"Car still detected. Waiting for it to pass... {distance:.2f} cm")
                time.sleep(1)
                
            servo.max()
            print("Closing Gate")
            time.sleep(2)
            platedetection()
            break

        time.sleep(1)
        
#cek validasi plat nomor
def send_to_api(text_ocr):
    try:
        response = requests.post(f"{api}checkOut", json={"idParkir": idparkir, "plateNumber": text_ocr})
        print(response.text)
        if response.status_code == 200:
            print("Berhasil masuk")
            open_gate()
            time.sleep(2)

    except requests.exceptions.HTTPError as err:
        print(err)
        
    platedetection()

#deteksi plat
def platedetection():
    cap = cv2.VideoCapture(0)
    cap.set(3,480)
    cap.set(4,480)
    
    start_time = None
    active_id = None 

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        results = modelyolo.track(frame, persist=True)
        current_time = time.time()

        if hasattr(results[0].boxes, 'id') and results[0].boxes.id is not None:
            boxes = results[0].boxes.xyxy.cpu().numpy().astype(int)
            ids = results[0].boxes.id.cpu().numpy().astype(int)

            if active_id is not None and active_id not in ids:
                print(f"Active ID {active_id} no longer detected.")
                active_id = None
                start_time = None

            for box, id in zip(boxes, ids):
                if active_id is None:
                    active_id = id
                    start_time = current_time
                    print(f"New active ID {id} detected.")

                if id == active_id:
                    cv2.rectangle(frame, (box[0], box[1]), (box[2], box[3]), (0, 255, 0), 2)
                    elapsed_time = current_time - start_time
                    cv2.putText(frame, f"Id {id} - {elapsed_time:.2f}s", (box[0], box[1]), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)

                    if elapsed_time > 3:
                        cropped_image = frame[box[1]:box[3], box[0]:box[2]]
                        image_path = "temp/cropped_image.jpg"
                        cv2.imwrite(image_path, cropped_image)
                        print(f"Cropped image for ID {id} saved successfully!")
                        active_id = None
                        start_time = None
                        cap.release()
                        cv2.destroyAllWindows()
                        segment_characters(image_path)

        cv2.imshow("frame", frame)
        if cv2.waitKey(1) & 0xFF == ord("q"):
            break

    cap.release()
    cv2.destroyAllWindows()
    
#preprocessing gambar
def preprocess_image(image_path):
    img = cv2.imread(image_path)
    img = cv2.resize(img, (40, 40))
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    
    img_array = tf.keras.preprocessing.image.img_to_array(img)
    img_array = tf.expand_dims(img_array, 0)
    img_array = img_array / 255.0

    return img_array

#prediksi karakter pada kontur
def predict_char(img_array):
    char_list = list(class_names)
    predictions = model.predict(img_array)
    predicted_class_index = np.argmax(predictions[0])
    predicted_class_name = char_list[predicted_class_index]
    
    return predicted_class_name

#segmentasi dengan contour dengan jarak tertentu
def segment_characters(image_path):
    img_plate_gray = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    img_plate_gray = cv2.resize(img_plate_gray, (333, 75))

    _, img_plate_bw = cv2.threshold(img_plate_gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

    kernel = cv2.getStructuringElement(cv2.MORPH_CROSS, (3, 3))
    img_plate_bw = cv2.morphologyEx(img_plate_bw, cv2.MORPH_OPEN, kernel)

    contours_plate, _ = cv2.findContours(img_plate_bw, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    index_chars_candidate = []

    for idx, contour_plate in enumerate(contours_plate):
        x, y, w, h = cv2.boundingRect(contour_plate)

        if h >= 20 and h <= 50 and w >= 20 and w<= 50: #can adjust the value
            index_chars_candidate.append(idx)

    if not index_chars_candidate:
        print('Karakter tidak tersegmentasi')
        return None

    score_chars_candidate = np.zeros(len(index_chars_candidate))

    for i, charA in enumerate(index_chars_candidate):
        xA, yA, wA, hA = cv2.boundingRect(contours_plate[charA])
        for j, charB in enumerate(index_chars_candidate):
            if charA == charB:
                continue
            xB, yB, wB, hB = cv2.boundingRect(contours_plate[charB])
            if abs(yA - yB) < 11:
                score_chars_candidate[i] += 1


    index_chars = [index_chars_candidate[i] for i in range(len(score_chars_candidate)) if score_chars_candidate[i] == np.max(score_chars_candidate)]
    x_coors = [cv2.boundingRect(contours_plate[char])[0] for char in index_chars]
    index_chars_sorted = [index_chars[i] for i in np.argsort(x_coors)]

    img_plate_thresh = cv2.cvtColor(img_plate_bw, cv2.COLOR_GRAY2BGR)

    ocr_results = []

    for idx, char_idx in enumerate(index_chars_sorted):
        x, y, w, h = cv2.boundingRect(contours_plate[char_idx])

        padding = 3
        x_with_padding = x - padding
        y_with_padding = y - padding
        w_with_padding = w + 2 * padding
        h_with_padding = h + 2 * padding
        cv2.rectangle(img_plate_thresh, (x_with_padding, y_with_padding), (x_with_padding + w_with_padding, y_with_padding + h_with_padding), (0, 255, 0), 1)

        character_crop = img_plate_bw[y:y+h, x:x+w]

        char_img = f"temp/char_{idx+1}.jpg"
        cv2.imwrite(char_img, character_crop)

        input_image = preprocess_image(char_img)
    
        text = predict_char(input_image)
        ocr_results.append(text)
        
    platenumber = ''.join(ocr_results)
    print('Plat Nomor:'+platenumber)
    send_to_api(platenumber)

servo.max()
platedetection()
