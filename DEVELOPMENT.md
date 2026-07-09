# Development Setup

## Prerequisites

- **Android Development**
  - Android Studio 2022.1 or newer
  - Android SDK 21 or newer
  - JDK 11 or newer

- **Backend Development**
  - Python 3.9 or newer
  - PostgreSQL 12 or newer
  - pip or conda for package management

- **General**
  - Git
  - Docker and Docker Compose (optional but recommended)

## Android Development Setup

### 1. Clone Repository
```bash
git clone https://github.com/HaniCodeHub/Livestock-Guardian.git
cd Livestock-Guardian
```

### 2. Open in Android Studio
- Launch Android Studio
- Click "Open Project"
- Navigate to `Livestock-Guardian/app`
- Wait for Gradle sync to complete

### 3. Configure SDK
- Go to Tools → SDK Manager
- Install desired Android SDK (21-34 recommended)
- Install Build Tools

### 4. Run the App
```bash
# Using Android Studio
- Click the green "Run" button (Shift + F10)

# Using Gradle
./gradlew assembleDebug
```

### 5. Run Tests
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

## Backend Development Setup

### 1. Clone Repository
```bash
git clone https://github.com/HaniCodeHub/Livestock-Guardian.git
cd Livestock-Guardian
```

### 2. Create Virtual Environment
```bash
# Using venv
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Or using conda
conda create -n livestock-guardian python=3.9
conda activate livestock-guardian
```

### 3. Install Dependencies
```bash
pip install -r requirements.txt
```

### 4. Configure Environment Variables
```bash
# Copy example file
cp .env.example .env

# Edit .env with your configuration
# Required variables:
# - DATABASE_URL
# - SUPABASE_URL
# - SUPABASE_KEY
# - API_SECRET_KEY
```

### 5. Setup Database
```bash
# Create database
createdb livestock_guardian

# Run migrations
python manage.py migrate
```

### 6. Run Development Server
```bash
uvicorn main:app --reload
```

The API will be available at `http://localhost:8000`

### 7. Run Tests
```bash
pytest
pytest --cov=.  # With coverage
```

## Docker Setup (Optional)

### 1. Build Docker Images
```bash
docker-compose build
```

### 2. Start Services
```bash
docker-compose up
```

This starts:
- PostgreSQL database on port 5432
- FastAPI backend on port 8000
- Redis cache on port 6379

### 3. Access Services
- Backend API: http://localhost:8000
- API Docs: http://localhost:8000/docs
- Database: localhost:5432

## IDE Configuration

### Android Studio
1. Go to File → Settings → Code Style
2. Choose the appropriate code style scheme
3. Enable "Reformat code on save" option

### VS Code / PyCharm (Backend)
1. Install Python extension
2. Configure Black formatter:
   ```json
   {
     "python.formatting.provider": "black",
     "python.linting.pylintEnabled": true,
     "python.linting.enabled": true
   }
   ```

## Running the Full Stack

### Using Docker Compose
```bash
# Start all services
docker-compose up

# Build and start fresh
docker-compose up --build

# Run in background
docker-compose up -d

# Stop services
docker-compose down
```

### Manual Setup
1. Start PostgreSQL
2. Start Redis (if using caching)
3. Start Backend: `uvicorn main:app --reload`
4. Start Android emulator or connect device
5. Run Android app from Android Studio

## Code Quality Tools

### Android (Kotlin)
```bash
# Lint
./gradlew lint

# Format code
./gradlew ktlintFormat

# Static analysis
./gradlew detekt
```

### Backend (Python)
```bash
# Format code
black .

# Lint
flake8 .

# Type checking
mypy .

# Security check
bandit -r .
```

## Useful Commands

### Git
```bash
# Create feature branch
git checkout -b feature/your-feature

# Commit with good message
git commit -m "Add feature description"

# Push to remote
git push origin feature/your-feature

# Create pull request (on GitHub)
```

### Database
```bash
# Backup database
pg_dump livestock_guardian > backup.sql

# Restore database
psql livestock_guardian < backup.sql

# Connect to database
psql livestock_guardian
```

### API Testing
```bash
# Using curl
curl -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Using httpie
http POST localhost:8000/auth/login \
  email=user@example.com \
  password=password

# Using Postman
# Import the API collection from docs/postman/
```

## Common Issues

### Port Already in Use
```bash
# Kill process using port 8000
lsof -i :8000
kill -9 <PID>

# On Windows
netstat -ano | findstr :8000
taskkill /PID <PID> /F
```

### Dependencies Not Installing
```bash
# Upgrade pip
pip install --upgrade pip

# Clear pip cache
pip cache purge

# Reinstall requirements
pip install --force-reinstall -r requirements.txt
```

### Database Connection Failed
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Check connection
psql -U postgres -d livestock_guardian

# Verify DATABASE_URL in .env
```

## Next Steps

1. Read the [Architecture](ARCHITECTURE.md) documentation
2. Review [API Documentation](API_DOCUMENTATION.md)
3. Check [Testing Guide](TESTING.md)
4. Follow [Contributing Guidelines](CONTRIBUTING.md)

Happy coding!
