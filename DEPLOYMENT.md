# Deployment Guide

## Prerequisites

- Docker and Docker Compose
- Python 3.9+
- Node.js 16+ (optional, for frontend)
- Supabase account and project
- AWS account (for hosting, optional)

## Environment Variables

Create a `.env` file in the project root:

```env
# Database
DATABASE_URL=postgresql://user:password@localhost:5432/livestock_guardian
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_KEY=your-supabase-key

# API
API_SECRET_KEY=your-secret-key
API_HOST=0.0.0.0
API_PORT=8000
API_ENVIRONMENT=production

# AI Model
MODEL_PATH=/models/muzzle_recognition_v1.pb
MODEL_CONFIDENCE_THRESHOLD=0.85

# AWS (optional)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_REGION=us-east-1
S3_BUCKET=livestock-guardian-images

# Frontend (if applicable)
REACT_APP_API_URL=https://api.livestock-guardian.com/v1
REACT_APP_SUPABASE_URL=https://your-project.supabase.co
```

## Docker Deployment

1. Build the Docker image:
```bash
docker build -t livestock-guardian:latest .
```

2. Run the container:
```bash
docker run -d \
  --name livestock-guardian \
  -p 8000:8000 \
  --env-file .env \
  livestock-guardian:latest
```

## Manual Deployment

### Backend Setup

1. Clone the repository:
```bash
git clone https://github.com/HaniCodeHub/Livestock-Guardian.git
cd Livestock-Guardian
```

2. Create virtual environment:
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

4. Configure database:
```bash
python manage.py migrate
```

5. Start the server:
```bash
uvicorn main:app --reload
```

### Android App Deployment

1. Build release APK:
```bash
./gradlew assembleRelease
```

2. Sign APK (if needed):
```bash
jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 \
  -keystore my-release-key.jks \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  alias_name
```

3. Install on device:
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

## Production Considerations

- Enable HTTPS
- Set up database backups
- Configure logging and monitoring
- Implement rate limiting
- Set up CDN for static assets
- Use environment-specific configurations
- Implement CI/CD pipeline
- Set up database replicas for high availability

## Monitoring

- Set up application monitoring (New Relic, DataDog, etc.)
- Configure log aggregation (ELK Stack, Splunk, etc.)
- Set up alerts for critical issues
- Monitor API response times and error rates
- Track database performance metrics

## Scaling

- Use load balancer for API
- Implement caching (Redis)
- Use database connection pooling
- Implement horizontal scaling with Kubernetes
- Use CDN for image distribution
- Consider microservices architecture as needed
