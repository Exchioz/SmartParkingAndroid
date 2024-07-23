import tensorflow as tf
from ultralytics import YOLO

idparkir = 2 #change

modelyolo = YOLO("models/best.onnx")
model = tf.keras.models.load_model("models/charplatevechile_recognition.keras")
class_names = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
api = "http://localhost:5000/" #change