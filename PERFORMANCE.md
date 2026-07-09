# Performance Optimization Guide

## Frontend Optimization (Android)

### 1. Image Loading Optimization
```kotlin
// Use image loading libraries with caching
val imageUrl = "https://example.com/muzzle.jpg"

// Bad: Direct loading
val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())

// Good: Use Glide or Coil with caching
Glide.with(this)
    .load(imageUrl)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView)
```

### 2. Memory Management
```kotlin
// Clear cache when not needed
val imageFile = File(context.cacheDir, "muzzle_temp.jpg")
imageFile.delete()

// Use weak references for large objects
val weakBitmap = WeakReference(bitmap)

// Recycle bitmaps
bitmap?.recycle()
```

### 3. Database Queries Optimization
```kotlin
// Bad: Loading all records
val allAnimals = database.getAllAnimals()

// Good: Pagination with limit
val animals = database.getAnimals(limit = 20, offset = 0)

// Good: Use indexes
database.createIndex("animals", "owner_id")
```

### 4. Network Optimization
```kotlin
// Implement request batching
val requests = listOf(request1, request2, request3)
val batchResponse = api.batchRequest(requests)

// Use compression
val interceptor = HttpLoggingInterceptor()
interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

// Implement retry logic with exponential backoff
val retrofit = Retrofit.Builder()
    .addInterceptor(RetryInterceptor(maxRetry = 3))
    .build()
```

---

## Backend Optimization (FastAPI)

### 1. Database Query Optimization
```python
# Use indexes for frequently queried columns
db.Index("idx_animals_owner_id", "animals.owner_id")

# Use select() to retrieve only needed columns
from sqlalchemy import select
query = select(Animal.id, Animal.name).filter(
    Animal.owner_id == user_id
)

# Use prefetch_related for related objects
animals = Animal.objects.prefetch_related('owner').all()
```

### 2. Caching Strategy
```python
from fastapi_cache2 import FastAPICache
from fastapi_cache2.backends.redis import RedisBackend
from redis import asyncio as aioredis

@app.get("/animals/{animal_id}")
@cached(expire=3600)  # Cache for 1 hour
async def get_animal(animal_id: str):
    return db.query(Animal).get(animal_id)

# Cache invalidation
def invalidate_animal_cache(animal_id: str):
    FastAPICache.clear(namespace=f"get_animal:{animal_id}")
```

### 3. API Response Optimization
```python
# Compress responses
from fastapi.middleware.gzip import GZIPMiddleware
app.add_middleware(GZIPMiddleware, minimum_size=1000)

# Implement pagination
@app.get("/animals")
async def list_animals(skip: int = 0, limit: int = 20):
    return db.query(Animal).offset(skip).limit(limit).all()

# Use lazy loading
class Animal(Base):
    owner = relationship("User", lazy="select")
```

### 4. Async/Concurrent Processing
```python
import asyncio
from concurrent.futures import ThreadPoolExecutor

# Process images concurrently
async def process_images_concurrent(image_urls):
    tasks = [process_image_async(url) for url in image_urls]
    results = await asyncio.gather(*tasks)
    return results

# Use thread pool for CPU-intensive tasks
executor = ThreadPoolExecutor(max_workers=4)
loop.run_in_executor(executor, cpu_intensive_task)
```

---

## AI/ML Model Optimization

### 1. Model Compression
```python
# Quantization
import tensorflow as tf
converter = tf.lite.TFLiteConverter.from_saved_model(model_dir)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_model = converter.convert()

# Pruning
from tensorflow_model_optimization.sparsity import keras
pruning_schedule = keras.PruningSchedule.PolynomialDecay(...)
```

### 2. Inference Optimization
```python
# Batch processing
predictions = model.predict_batch(images, batch_size=32)

# Use GPU acceleration
with tf.device('/GPU:0'):
    predictions = model.predict(image)

# Model serving with optimization
from tensorflow_serving import model_server
```

---

## Load Testing

### 1. Tools
- **Apache JMeter**: Load testing tool
- **Locust**: Python-based load testing
- **Artillery**: Node.js load testing

### 2. Load Test Example
```python
from locust import HttpUser, task, between

class AnimalUser(HttpUser):
    wait_time = between(1, 5)
    
    @task
    def list_animals(self):
        self.client.get("/animals")
    
    @task
    def identify_animal(self):
        with open("test_image.jpg", "rb") as f:
            self.client.post(
                "/identify/muzzle",
                files={"file": f}
            )
```

### 3. Expected Performance
- **Requests per second**: 1000+ RPS
- **Response time**: < 200ms (p95)
- **Error rate**: < 0.1%
- **Throughput**: > 500 MB/s

---

## Monitoring & Profiling

### 1. APM Tools
- New Relic
- DataDog
- Splunk
- ELK Stack

### 2. Profiling
```python
# CPU profiling
import cProfile
cProfile.run('expensive_function()')

# Memory profiling
from memory_profiler import profile
@profile
def expensive_memory_function():
    pass

# Line profiling
kernprof -lv script.py
```

---

## Optimization Checklist

- [ ] Database indexes created for common queries
- [ ] API responses paginated
- [ ] Caching strategy implemented
- [ ] Images compressed and optimized
- [ ] Unnecessary network requests eliminated
- [ ] Database connection pooling enabled
- [ ] Gzip compression enabled
- [ ] CDN configured for static assets
- [ ] Load testing performed
- [ ] Performance baselines established
- [ ] Monitoring dashboards configured
- [ ] Slow queries identified and optimized

---

## Performance Benchmarks

### Target Metrics
| Metric | Target |
|--------|--------|
| Page Load Time | < 3s |
| API Response Time | < 200ms |
| Image Upload | < 5s |
| Identification | < 3s |
| Database Query | < 100ms |
| CPU Usage | < 70% |
| Memory Usage | < 80% |
| Disk I/O | < 100ms |

---

## References
- [FastAPI Performance](https://fastapi.tiangolo.com/advanced/performance/)
- [Android Performance](https://developer.android.com/topic/performance)
- [Database Query Optimization](https://use-the-index-luke.com/)
