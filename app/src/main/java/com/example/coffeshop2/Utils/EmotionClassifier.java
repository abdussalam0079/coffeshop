package com.example.coffeshop2.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;

import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * TensorFlow Lite Emotion Classifier
 * Loads and runs TFLite model for emotion detection
 */
public class EmotionClassifier {
    
    private static final String MODEL_FILE = "emotion_model.tflite";
    private static final int INPUT_SIZE = 48; // Model input size (adjust based on your model)
    private static final int NUM_CLASSES = 7; // Number of emotion classes
    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;
    
    private Interpreter tflite;
    private ByteBuffer inputBuffer;
    private float[][] outputArray;
    private String[] labels = {"angry", "disgust", "fear", "happy", "neutral", "sad", "surprise"};
    
    public EmotionClassifier(Context context) throws IOException {
        try {
            MappedByteBuffer modelBuffer = loadModelFile(context);
            if (modelBuffer != null) {
                tflite = new Interpreter(modelBuffer);
                inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
                inputBuffer.order(ByteOrder.nativeOrder());
                outputArray = new float[1][NUM_CLASSES];
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e; // Re-throw to indicate model loading failed
        }
    }
    
    public boolean isModelLoaded() {
        return tflite != null;
    }
    
    /**
     * Load TFLite model from assets
     */
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        inputStream.close();
        return buffer;
    }
    
    /**
     * Preprocess image for model input
     */
    private ByteBuffer preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);
        inputBuffer.rewind();
        
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, 
                resizedBitmap.getWidth(), resizedBitmap.getHeight());
        
        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];
                // Convert RGB to grayscale and normalize
                float grayValue = ((val >> 16) & 0xFF) * 0.299f + 
                                 ((val >> 8) & 0xFF) * 0.587f + 
                                 (val & 0xFF) * 0.114f;
                inputBuffer.putFloat((grayValue - IMAGE_MEAN) / IMAGE_STD);
            }
        }
        
        return inputBuffer;
    }
    
    /**
     * Classify emotion from bitmap
     */
    public String classifyEmotion(Bitmap bitmap) {
        if (tflite == null) {
            return "neutral"; // Fallback if model not loaded
        }
        
        ByteBuffer processedImage = preprocessImage(bitmap);
        tflite.run(processedImage, outputArray);
        
        // Get the index of the highest probability
        int maxIndex = 0;
        float maxValue = outputArray[0][0];
        for (int i = 1; i < NUM_CLASSES; i++) {
            if (outputArray[0][i] > maxValue) {
                maxValue = outputArray[0][i];
                maxIndex = i;
            }
        }
        
        return labels[maxIndex];
    }
    
    /**
     * Close interpreter
     */
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}

