package com.example.coffeshop2.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * TensorFlow Lite Emotion Classifier
 * Loads and runs TFLite model for emotion detection
 */
public class EmotionClassifier {

    private static final String TAG = "EmotionClassifier";
    // Primary expected model name (user-provided)
    private static final String MODEL_FILE = "model.tflite";
    // Fallback to legacy name if needed
    private static final String FALLBACK_MODEL_FILE = "emotion_model.tflite";
    // Model input size from README: 64x64 RGB
    private static final int INPUT_SIZE = 64;
    // Exported model outputs 7 classes in this fixed order (per dataset labels):
    // 0=anger,1=contempt,2=disgust,3=fear,4=happy,5=sadness,6=surprise
    private static final int NUM_CLASSES = 7;
    private static final float IMAGE_SCALE = 1.0f / 255.0f;

    private Interpreter tflite;
    private ByteBuffer inputBuffer;
    private float[][] outputArray;
    private String[] labels = {
            "anger",
            "contempt",
            "disgust",
            "fear",
            "happy",
            "sadness",
            "surprise"
    };
    
    public EmotionClassifier(Context context) throws IOException {
        try {
            MappedByteBuffer modelBuffer = loadModelFile(context);
            if (modelBuffer != null) {
                tflite = new Interpreter(modelBuffer);
                // Allocate buffer for RGB image (3 channels)
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
        // Try primary model first, then fallback
        String[] candidates = {MODEL_FILE, FALLBACK_MODEL_FILE};
        IOException lastException = null;
        for (String candidate : candidates) {
            try {
                AssetFileDescriptor fileDescriptor = context.getAssets().openFd(candidate);
                FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
                FileChannel fileChannel = inputStream.getChannel();
                long startOffset = fileDescriptor.getStartOffset();
                long declaredLength = fileDescriptor.getDeclaredLength();
                MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
                inputStream.close();
                return buffer;
            } catch (IOException e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw lastException;
        } else {
            throw new IOException("Model file not found in assets.");
        }
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
                // Normalize RGB to 0-1 scale
                inputBuffer.putFloat(((val >> 16) & 0xFF) * IMAGE_SCALE);
                inputBuffer.putFloat(((val >> 8) & 0xFF) * IMAGE_SCALE);
                inputBuffer.putFloat((val & 0xFF) * IMAGE_SCALE);
            }
        }

        return inputBuffer;
    }
    
    /**
     * Classify emotion from bitmap
     */
    public String classifyEmotion(Bitmap bitmap) {
        int index = classifyEmotionIndex(bitmap);
        return labels[index];
    }

    /**
     * Returns the index of the most probable class (aligned to labels array).
     */
    public int classifyEmotionIndex(Bitmap bitmap) {
        if (tflite == null) {
            Log.w(TAG, "TFLite interpreter not loaded; defaulting to index 0");
            return 0; // fallback to first class when model not loaded
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

        Log.d(TAG, "Output=" + Arrays.toString(outputArray[0]) + " maxIndex=" + maxIndex + " label=" + labels[maxIndex]);
        return maxIndex;
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

