# Backend Development Guidelines

## Project Structure

```
livestock-guardian/
├── main.py
├── requirements.txt
├── .env.example
├── app/
│   ├── __init__.py
│   ├── models/
│   │   ├── __init__.py
│   │   ├── user.py
│   │   ├── animal.py
│   │   └── identification.py
│   ├── schemas/
│   │   ├── __init__.py
│   │   ├── user.py
│   │   └── animal.py
│   ├── routes/
│   │   ├── __init__.py
│   │   ├── auth.py
│   │   ├── animals.py
│   │   └── identification.py
│   ├── services/
│   │   ├── __init__.py
│   │   ├── auth_service.py
│   │   ├── animal_service.py
│   │   └── identification_service.py
│   ├── utils/
│   │   ├── __init__.py
│   │   ├── security.py
│   │   └── validators.py
│   └── database/
│       ├── __init__.py
│       ├── session.py
│       └── models.py
├── tests/
│   ├── __init__.py
│   ├── test_auth.py
│   ├── test_animals.py
│   └── test_identification.py
├── migrations/
│   └── versions/
└── docker/
    └── Dockerfile
```

## Coding Standards

### Python Style Guide (PEP 8)

**Naming Conventions**:
```python
# Classes: PascalCase
class AnimalRegistration:
    pass

# Functions/Variables: snake_case
def register_animal(animal_data):
    pass

# Constants: UPPER_SNAKE_CASE
DEFAULT_PAGE_SIZE = 20
MAX_IMAGE_SIZE = 10_000_000
```

**Formatting**:
```python
# Use type hints
from typing import List, Optional

def get_animals(
    owner_id: str,
    limit: int = 20,
    offset: int = 0
) -> List[Animal]:
    """Retrieve paginated list of animals for owner."""
    pass

# Use docstrings
class AnimalService:
    """Service for managing animal operations."""
    
    def register_animal(self, data: dict) -> Animal:
        """
        Register a new animal.
        
        Args:
            data: Animal registration data
            
        Returns:
            Newly created animal
            
        Raises:
            ValidationError: If data is invalid
        """
        pass
```

## FastAPI Application

### Application Setup
```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZIPMiddleware
from sqlalchemy.orm import sessionmaker

app = FastAPI(
    title="Livestock Guardian API",
    description="AI-powered livestock identification API",
    version="2.0.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://app.livestock-guardian.com"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Compression
app.add_middleware(GZIPMiddleware, minimum_size=1000)

# Database
SessionLocal = sessionmaker(bind=engine)

# Include routers
app.include_router(auth.router, prefix="/api/v1/auth", tags=["Auth"])
app.include_router(animals.router, prefix="/api/v1/livestock", tags=["Livestock"])
app.include_router(identification.router, prefix="/api/v1/identify", tags=["Identification"])
```

### Route Example
```python
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

router = APIRouter()

@router.post("/register", response_model=AnimalResponse, status_code=status.HTTP_201_CREATED)
async def register_animal(
    data: AnimalRegisterRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
) -> AnimalResponse:
    """Register a new animal."""
    
    # Validate data
    if not data.name:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Animal name is required"
        )
    
    # Create animal
    animal = Animal(
        owner_id=current_user.id,
        name=data.name,
        species=data.species,
        breed=data.breed
    )
    
    db.add(animal)
    db.commit()
    db.refresh(animal)
    
    return AnimalResponse.from_orm(animal)
```

## Database

### SQLAlchemy Models
```python
from sqlalchemy import Column, String, DateTime, ForeignKey, Integer
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()

class User(Base):
    __tablename__ = "users"
    
    id = Column(String, primary_key=True)
    email = Column(String, unique=True, index=True)
    password_hash = Column(String)
    name = Column(String)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relationships
    animals = relationship("Animal", back_populates="owner")

class Animal(Base):
    __tablename__ = "animals"
    
    id = Column(String, primary_key=True)
    owner_id = Column(String, ForeignKey("users.id"))
    name = Column(String, index=True)
    species = Column(String)
    breed = Column(String)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relationships
    owner = relationship("User", back_populates="animals")
    biometrics = relationship("MuzzleBiometric", back_populates="animal")
```

### Pydantic Schemas
```python
from pydantic import BaseModel, EmailStr, validator
from typing import Optional
from datetime import datetime

class AnimalRegisterRequest(BaseModel):
    name: str
    species: str
    breed: Optional[str] = None
    
    @validator('name')
    def name_not_empty(cls, v):
        if not v or not v.strip():
            raise ValueError('Name cannot be empty')
        return v.strip()

class AnimalResponse(BaseModel):
    id: str
    name: str
    species: str
    breed: Optional[str]
    created_at: datetime
    
    class Config:
        from_attributes = True
```

## Authentication

### JWT Implementation
```python
from fastapi import Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthCredentials
from datetime import datetime, timedelta
import jwt

ALGORITHM = "HS256"
SECRET_KEY = os.getenv("SECRET_KEY")
ACCESS_TOKEN_EXPIRE_MINUTES = 1440

def create_access_token(data: dict) -> str:
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

async def get_current_user(
    credentials: HTTPAuthCredentials = Depends(HTTPBearer()),
    db: Session = Depends(get_db)
) -> User:
    try:
        payload = jwt.decode(
            credentials.credentials,
            SECRET_KEY,
            algorithms=[ALGORITHM]
        )
        user_id = payload.get("sub")
        
        user = db.query(User).filter(User.id == user_id).first()
        if not user:
            raise HTTPException(status_code=401, detail="User not found")
        
        return user
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")
```

## Error Handling

```python
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request, exc):
    return JSONResponse(
        status_code=400,
        content={
            "error": "validation_error",
            "message": "Request validation failed",
            "details": exc.errors()
        }
    )

@app.exception_handler(HTTPException)
async def http_exception_handler(request, exc):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "error": "http_error",
            "message": exc.detail
        }
    )
```

## Logging

```python
import logging
from pythonjsonlogger import jsonlogger

# Setup JSON logging
logger = logging.getLogger()
handler = logging.StreamHandler()
formatter = jsonlogger.JsonFormatter()
handler.setFormatter(formatter)
logger.addHandler(handler)

# Log usage
@app.middleware("http")
async def log_request(request, call_next):
    logger.info({
        "method": request.method,
        "path": request.url.path,
        "client": request.client.host
    })
    
    response = await call_next(request)
    return response
```

## Testing

### Test Structure
```python
import pytest
from fastapi.testclient import TestClient

@pytest.fixture
def client():
    return TestClient(app)

@pytest.fixture
def sample_user():
    return {
        "email": "test@example.com",
        "password": "TestPassword123!"
    }

def test_register_animal(client, auth_token):
    response = client.post(
        "/api/v1/livestock/register",
        json={
            "name": "Bessie",
            "species": "cattle",
            "breed": "Holstein"
        },
        headers={"Authorization": f"Bearer {auth_token}"}
    )
    assert response.status_code == 201
    assert response.json()["name"] == "Bessie"
```

## Performance Tips

1. Use database indexes
2. Implement pagination
3. Add caching (Redis)
4. Use async/await
5. Optimize queries
6. Monitor slow queries
7. Use connection pooling
8. Implement rate limiting

## Security Best Practices

1. Use HTTPS only
2. Validate all inputs
3. Use parameterized queries
4. Hash passwords properly
5. Implement rate limiting
6. Use CORS wisely
7. Sanitize outputs
8. Keep dependencies updated

## Deployment Checklist

- [ ] Environment variables configured
- [ ] Database migrations run
- [ ] Tests passing
- [ ] Security audit completed
- [ ] Performance tested
- [ ] Logging configured
- [ ] Error handling implemented
- [ ] Documentation updated
- [ ] HTTPS enabled
- [ ] Rate limiting enabled
