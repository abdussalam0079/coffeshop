# TensorFlow Lite Model Setup Guide

## Steps to Add Your Emotion Detection Model

1. **Download or Train Your TFLite Model**
   - Model should detect 7 emotions: angry, disgust, fear, happy, neutral, sad, surprise
   - Input size: 48x48 grayscale (adjust in EmotionClassifier.java if different)
   - Output: 7 float values (probabilities for each emotion)

2. **Add Model to Assets**
   - Create `app/src/main/assets/` folder if it doesn't exist
   - Place your `emotion_model.tflite` file in the assets folder
   - Update `MODEL_FILE` in `EmotionClassifier.java` if using a different filename

3. **Adjust Model Parameters** (if needed)
   - Update `INPUT_SIZE` in `EmotionClassifier.java` to match your model's input size
   - Update `NUM_CLASSES` if your model detects different number of emotions
   - Update `labels` array to match your model's output labels

4. **Test the Integration**
   - Run the app and take a selfie
   - The app will automatically use the TFLite model if available
   - Falls back to simulated detection if model is not found

## Model Requirements

- Format: TensorFlow Lite (.tflite)
- Input: Grayscale image (48x48 recommended)
- Output: 7 emotion probabilities
- Labels: ["angry", "disgust", "fear", "happy", "neutral", "sad", "surprise"]

## Fallback Behavior

If the model file is not found or fails to load, the app will use simulated mood detection for testing purposes.

