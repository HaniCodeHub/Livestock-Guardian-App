# Android Development Guidelines

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/livestockguardian/
│   │   │   ├── ui/
│   │   │   │   ├── home/
│   │   │   │   ├── registration/
│   │   │   │   ├── identification/
│   │   │   │   └── settings/
│   │   │   ├── viewmodel/
│   │   │   ├── repository/
│   │   │   ├── database/
│   │   │   ├── network/
│   │   │   ├── utils/
│   │   │   └── MainActivity.kt
│   │   └── res/
│   │       ├── layout/
│   │       ├── drawable/
│   │       ├── values/
│   │       └── menu/
│   ├── test/
│   │   └── Unit tests
│   └── androidTest/
│       └── Instrumentation tests
├── build.gradle.kts
└── proguard-rules.pro
```

## Coding Standards

### Kotlin Style Guide

**Naming Conventions**:
```kotlin
// Classes: PascalCase
class AnimalRegistration

// Properties: camelCase
private val userName: String

// Constants: UPPER_SNAKE_CASE
companion object {
    const val DEFAULT_TIMEOUT = 5000
}

// Functions: camelCase
fun registerAnimal(animal: Animal) { }

// Private properties with underscore prefix
private val _animals = MutableLiveData<List<Animal>>()
val animals: LiveData<List<Animal>> = _animals
```

**Formatting**:
```kotlin
// Single line if for simple statements
if (condition) doSomething()

// Multi-line for complex logic
if (condition) {
    doSomething()
    doSomethingElse()
}

// Use when instead of if-else chains
when (status) {
    Status.ACTIVE -> handleActive()
    Status.INACTIVE -> handleInactive()
    else -> handleUnknown()
}
```

## Architecture: MVVM

### ViewModel Example
```kotlin
class AnimalViewModel(
    private val animalRepository: AnimalRepository
) : ViewModel() {
    
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState
    
    private val _animals = MutableLiveData<List<Animal>>()
    val animals: LiveData<List<Animal>> = _animals
    
    fun loadAnimals() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val data = animalRepository.getAnimals()
                _animals.value = data
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

### Fragment Example
```kotlin
class AnimalListFragment : Fragment() {
    
    private val viewModel: AnimalViewModel by viewModels()
    private lateinit var binding: FragmentAnimalListBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding = FragmentAnimalListBinding.bind(view)
        
        viewModel.animals.observe(viewLifecycleOwner) { animals ->
            updateUI(animals)
        }
        
        viewModel.loadAnimals()
    }
    
    private fun updateUI(animals: List<Animal>) {
        // Update UI with animals
    }
}
```

## UI Components

### Using View Binding
```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.buttonRegister.setOnClickListener {
            // Handle click
        }
    }
}
```

### Material Design Components
```xml
<com.google.android.material.button.MaterialButton
    android:id="@+id/button_register"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Register"
    app:cornerRadius="8dp"
    />

<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Animal Name">
    
    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/edit_animal_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        />
</com.google.android.material.textfield.TextInputLayout>
```

## Camera Integration

### Using CameraX
```kotlin
class CameraViewModel : ViewModel() {
    
    fun captureImage(
        file: File,
        onSuccess: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val imageCapture = ImageCapture.Builder()
            .setTargetResolution(Size(1080, 1080))
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
        
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file)
            .build()
        
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSuccess(file)
                }
                
                override fun onError(exc: ImageCaptureException) {
                    onError(exc)
                }
            }
        )
    }
}
```

## Permissions

### Requesting Permissions
```kotlin
private val cameraPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        openCamera()
    } else {
        showPermissionDeniedDialog()
    }
}

fun requestCameraPermission() {
    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
}
```

### Declaring in Manifest
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## Networking

### Retrofit Setup
```kotlin
object ApiClient {
    private const val BASE_URL = "https://api.livestock-guardian.com/"
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

## Database

### Room Database Example
```kotlin
@Entity
data class Animal(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val ownerId: String
)

@Dao
interface AnimalDao {
    @Insert
    suspend fun insertAnimal(animal: Animal)
    
    @Query("SELECT * FROM Animal WHERE id = :id")
    suspend fun getAnimal(id: String): Animal
    
    @Query("SELECT * FROM Animal WHERE ownerId = :ownerId")
    suspend fun getAnimalsByOwner(ownerId: String): List<Animal>
}

@Database(entities = [Animal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
}
```

## Testing

### Unit Test Example
```kotlin
class AnimalViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var viewModel: AnimalViewModel
    private val mockRepository = mockk<AnimalRepository>()
    
    @Before
    fun setup() {
        viewModel = AnimalViewModel(mockRepository)
    }
    
    @Test
    fun `loadAnimals returns data successfully`() = runTest {
        // Arrange
        val animals = listOf(
            Animal("1", "Bessie", "cattle", "user123")
        )
        coEvery { mockRepository.getAnimals() } returns animals
        
        // Act
        viewModel.loadAnimals()
        
        // Assert
        assert(viewModel.animals.value == animals)
    }
}
```

## Best Practices

1. **Use Kotlin coroutines** for async operations
2. **Use LiveData** for state management
3. **Use dependency injection** (Hilt)
4. **Follow MVVM architecture**
5. **Use view binding** instead of findViewById
6. **Write unit tests** for ViewModels
7. **Handle permissions** properly
8. **Optimize memory usage**
9. **Use background threads** for heavy operations
10. **Follow Android naming conventions**

## Performance Tips

- Use ProGuard for production builds
- Optimize images (compress, resize)
- Lazy load data
- Use pagination for lists
- Implement proper lifecycle management
- Monitor battery usage
- Test on real devices

## Security Considerations

- Never hardcode API keys
- Use HTTPS for all network calls
- Encrypt sensitive data
- Validate user input
- Use ProGuard obfuscation
- Don't log sensitive data
- Keep dependencies updated

## Build Variants

```gradle
android {
    buildTypes {
        debug {
            debuggable true
            minifyEnabled false
        }
        release {
            debuggable false
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
```

## Useful Libraries

- **AndroidX**: Core libraries
- **CameraX**: Camera functionality
- **Retrofit**: HTTP client
- **Room**: Local database
- **Hilt**: Dependency injection
- **Compose**: Modern UI
- **DataStore**: SharedPreferences replacement
- **WorkManager**: Background tasks
