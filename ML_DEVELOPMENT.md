# AI/ML Model Development

## Model Architecture

### Muzzle Recognition Model

**Architecture**: ResNet-50 with fine-tuning
```python
import tensorflow as tf

def create_muzzle_model():
    base_model = tf.keras.applications.ResNet50(
        input_shape=(224, 224, 3),
        include_top=False,
        weights='imagenet'
    )
    
    # Freeze base layers
    base_model.trainable = False
    
    # Add custom layers
    model = tf.keras.Sequential([
        base_model,
        tf.keras.layers.GlobalAveragePooling2D(),
        tf.keras.layers.Dense(1024, activation='relu'),
        tf.keras.layers.Dropout(0.5),
        tf.keras.layers.Dense(512, activation='relu'),
        tf.keras.layers.Dense(128, activation='relu'),
    ])
    
    return model
```

## Training Pipeline

### Data Preparation
```python
def prepare_training_data():
    # Load images
    train_images = load_images('data/train/')
    val_images = load_images('data/val/')
    
    # Preprocess
    train_images = preprocess_images(train_images)
    val_images = preprocess_images(val_images)
    
    # Data augmentation
    augmented_train = augment_data(train_images)
    
    return augmented_train, val_images

def augment_data(images):
    augmentation = tf.keras.Sequential([
        tf.keras.layers.RandomFlip("horizontal"),
        tf.keras.layers.RandomRotation(0.2),
        tf.keras.layers.RandomZoom(0.2),
        tf.keras.layers.RandomBrightness(0.2),
    ])
    return augmentation(images)
```

### Model Training
```python
def train_muzzle_model(train_data, val_data, epochs=100):
    model = create_muzzle_model()
    
    # Compile
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=1e-4),
        loss='cosine_similarity',
        metrics=['accuracy']
    )
    
    # Callbacks
    callbacks = [
        tf.keras.callbacks.EarlyStopping(
            monitor='val_loss',
            patience=10,
            restore_best_weights=True
        ),
        tf.keras.callbacks.ReduceLROnPlateau(
            monitor='val_loss',
            factor=0.5,
            patience=5,
            min_lr=1e-7
        ),
        tf.keras.callbacks.ModelCheckpoint(
            'best_model.h5',
            save_best_only=True
        )
    ]
    
    # Train
    history = model.fit(
        train_data,
        validation_data=val_data,
        epochs=epochs,
        callbacks=callbacks
    )
    
    return model, history
```

## Evaluation Metrics

### Performance Metrics
```python
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score

def evaluate_model(model, test_data, test_labels):
    predictions = model.predict(test_data)
    pred_labels = np.argmax(predictions, axis=1)
    
    accuracy = accuracy_score(test_labels, pred_labels)
    precision = precision_score(test_labels, pred_labels, average='weighted')
    recall = recall_score(test_labels, pred_labels, average='weighted')
    f1 = f1_score(test_labels, pred_labels, average='weighted')
    
    return {
        'accuracy': accuracy,
        'precision': precision,
        'recall': recall,
        'f1': f1
    }
```

### Target Metrics
- **Accuracy**: > 95%
- **Precision**: > 93%
- **Recall**: > 93%
- **F1 Score**: > 93%

## Model Deployment

### Model Export
```python
# Convert to TFLite for mobile
converter = tf.lite.TFLiteConverter.from_saved_model('saved_model/')
converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_model = converter.convert()

with open('model.tflite', 'wb') as f:
    f.write(tflite_model)
```

### Model Serving
```python
import onnx
import onnxruntime as rt

# Convert to ONNX
from tf2onnx import convert
import tf2onnx

# Load and serve
sess = rt.InferenceSession("model.onnx")
input_name = sess.get_inputs()[0].name
label_name = sess.get_outputs()[0].name

predictions = sess.run(
    [label_name],
    {input_name: test_data.astype(np.float32)}
)
```

## Continuous Model Improvement

### Data Collection
- Collect user submissions regularly
- Get feedback on matches
- Build diverse dataset
- Account for seasonal variations

### Retraining Pipeline
```bash
# Monthly retraining
0 2 1 * * /scripts/retrain_model.sh

# Validation before deployment
/scripts/validate_model.py new_model.h5 --threshold=0.95

# Gradual rollout (canary)
/scripts/deploy_model.py new_model.h5 --percentage=5
```

### A/B Testing
```python
# Test two models simultaneously
def inference_with_ab_test(image, user_id):
    if hash(user_id) % 2 == 0:
        # Model A
        result = model_a.predict(image)
        log_inference('model_a', result)
    else:
        # Model B
        result = model_b.predict(image)
        log_inference('model_b', result)
    
    return result
```

## Monitoring & Debugging

### Model Performance Monitoring
```python
def log_inference(image_id, prediction, confidence, actual_animal_id):
    db.insert('inference_logs', {
        'image_id': image_id,
        'prediction': prediction,
        'confidence': confidence,
        'actual_animal_id': actual_animal_id,
        'timestamp': datetime.now(),
        'correct': prediction == actual_animal_id
    })

# Daily accuracy check
def calculate_daily_accuracy():
    logs = db.query('inference_logs').where(
        'timestamp > NOW() - INTERVAL 1 day'
    )
    accuracy = sum(l.correct for l in logs) / len(logs)
    alert_if_below_threshold(accuracy, 0.93)
```

### Drift Detection
```python
from evidently.report import Report
from evidently.metric_preset import DataDriftPreset

# Monitor data drift
report = Report(metrics=[DataDriftPreset()])
report.run(
    reference_data=baseline_data,
    current_data=new_data
)

if report.show()['metrics'][0]['result']['is_data_drift']:
    trigger_model_retraining()
```

## Feature Extraction

### Muzzle Features
```python
def extract_muzzle_features(image):
    # Preprocess image
    image = preprocess_image(image)
    
    # Extract embeddings
    feature_vector = model(image)
    
    # Normalize
    feature_vector = tf.nn.l2_normalize(feature_vector)
    
    return feature_vector

# Compare features
def similarity_score(features1, features2):
    # Cosine similarity
    similarity = tf.reduce_sum(features1 * features2, axis=-1)
    return similarity.numpy()
```

## Model Versioning

### Tracking
```python
model_versions = {
    'v1.0': {
        'accuracy': 0.92,
        'released': '2024-01-01',
        'notes': 'Initial model'
    },
    'v1.1': {
        'accuracy': 0.94,
        'released': '2024-01-15',
        'notes': 'Improved training data'
    },
    'v2.0': {
        'accuracy': 0.96,
        'released': '2024-02-01',
        'notes': 'New architecture'
    }
}
```

## Bias & Fairness Testing

### Testing Framework
```python
def test_model_fairness():
    # Test across different breeds
    for breed in ['Holstein', 'Angus', 'Jersey']:
        breed_data = get_test_data(breed=breed)
        accuracy = evaluate_model(breed_data)
        assert accuracy > 0.93, f"Poor accuracy for {breed}"
    
    # Test across lighting conditions
    for condition in ['bright', 'dim', 'overcast']:
        cond_data = get_test_data(lighting=condition)
        accuracy = evaluate_model(cond_data)
        assert accuracy > 0.93, f"Poor accuracy for {condition}"
```

## Documentation

### Model Card Template
```markdown
# Muzzle Recognition Model v2.0

## Model Details
- **Version**: 2.0
- **Architecture**: ResNet-50
- **Input**: 224x224 RGB image
- **Output**: 128-dim feature vector

## Performance
- **Accuracy**: 96.2%
- **Precision**: 96.1%
- **Recall**: 96.3%

## Training Data
- **Size**: 50,000 muzzle images
- **Breeds**: 20 cattle breeds
- **Augmentation**: Yes

## Limitations
- Works best with clear muzzle photos
- Accuracy decreases in poor lighting
- Not trained on other livestock species

## Ethical Considerations
- Used only for livestock identification
- No personal data stored
- Privacy-preserving
```

## ML Checklist

- [ ] Data quality validated
- [ ] Model tested on holdout set
- [ ] Performance meets requirements
- [ ] Bias testing completed
- [ ] Model documented
- [ ] Version tracked
- [ ] Monitoring set up
- [ ] Rollback plan ready
- [ ] Retraining scheduled
- [ ] User feedback mechanism in place
