# Testing Guide

## Unit Tests

### Android Unit Tests

Create test files in `app/src/test/kotlin/`:

```kotlin
import org.junit.Test
import org.junit.Before
import com.example.livestockguardian.domain.Animal
import com.example.livestockguardian.repository.AnimalRepository
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AnimalRepositoryTest {
    
    @Mock
    private lateinit var mockApiService: ApiService
    
    private lateinit var repository: AnimalRepository
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = AnimalRepository(mockApiService)
    }
    
    @Test
    fun testRegisterAnimal_Success() {
        // Arrange
        val animal = Animal(
            name = "Bessie",
            species = "cattle",
            breed = "Holstein"
        )
        
        // Act
        val result = repository.registerAnimal(animal)
        
        // Assert
        assert(result.isSuccess)
    }
}
```

Run Android tests:
```bash
./gradlew test
./gradlew connectedAndroidTest  # For instrumented tests
```

### Backend Tests

Create test files in `backend/tests/`:

```python
import pytest
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

@pytest.fixture
def sample_animal():
    return {
        "name": "Bessie",
        "species": "cattle",
        "breed": "Holstein"
    }

def test_register_animal(sample_animal):
    response = client.post(
        "/livestock/register",
        json=sample_animal,
        headers={"Authorization": "Bearer test_token"}
    )
    assert response.status_code == 201
    assert response.json()["name"] == "Bessie"

def test_get_animal():
    response = client.get(
        "/livestock/animal_123",
        headers={"Authorization": "Bearer test_token"}
    )
    assert response.status_code == 200
```

Run backend tests:
```bash
pytest
pytest --cov=.  # With coverage
pytest --cov=. --cov-report=html  # HTML report
```

## Integration Tests

Test interactions between multiple components:

```python
@pytest.mark.integration
def test_end_to_end_animal_registration():
    # Register user
    user_response = client.post("/auth/register", json={...})
    token = user_response.json()["token"]
    
    # Register animal
    animal_response = client.post(
        "/livestock/register",
        json={...},
        headers={"Authorization": f"Bearer {token}"}
    )
    
    # Verify animal was created
    get_response = client.get(
        f"/livestock/{animal_response.json()['id']}",
        headers={"Authorization": f"Bearer {token}"}
    )
    
    assert get_response.status_code == 200
```

## UI/E2E Tests

For Android UI testing with Espresso:

```kotlin
@RunWith(AndroidJUnit4::class)
class AnimalRegistrationActivityTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(AnimalRegistrationActivity::class.java)
    
    @Test
    fun testRegisterAnimalUI() {
        onView(withId(R.id.editTextName))
            .perform(typeText("Bessie"))
        
        onView(withId(R.id.editTextSpecies))
            .perform(typeText("Cattle"))
        
        onView(withId(R.id.buttonRegister))
            .perform(click())
        
        onView(withText("Animal registered successfully"))
            .check(matches(isDisplayed()))
    }
}
```

## Performance Tests

Monitor and test performance metrics:

```python
import time

@pytest.mark.performance
def test_image_upload_performance():
    start = time.time()
    
    response = client.post(
        "/identify/muzzle",
        files={"file": ("test.jpg", open("test.jpg", "rb"))},
        headers={"Authorization": "Bearer test_token"}
    )
    
    duration = time.time() - start
    
    assert response.status_code == 200
    assert duration < 5.0  # Should complete within 5 seconds
```

## Test Coverage Goals

- Aim for 80%+ code coverage
- 100% coverage for critical business logic
- Focus on happy path and error scenarios
- Test edge cases and boundary conditions

## Running All Tests

```bash
# Android
./gradlew test connectedAndroidTest

# Backend
pytest --cov=. --cov-report=term-missing

# Combined report
pytest --cov=. --cov-report=html
```

## Continuous Integration

Tests should run automatically on:
- Every commit (pre-commit hooks)
- Every push (CI/CD pipeline)
- Before deployment
