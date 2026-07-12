# Livestock Guardian 🐄

![Livestock Guardian](screenshorts/0_livestock_guardian_logo.png)

AI-Powered Livestock Identification & Theft Prevention System

An Android application that uses biometric muzzle recognition, artificial intelligence, and cloud technologies to uniquely identify livestock, verify ownership, and reduce animal theft.

This project is actively being improved for practical farm and livestock management use by combining smart recognition with real-world livestock operations.

It aims to support safer animal tracking and ownership verification in real-world agricultural settings.

The app is designed to be practical, scalable, and useful for modern livestock management workflows.

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Backend](https://img.shields.io/badge/Backend-FastAPI-blue)
![Database](https://img.shields.io/badge/Database-Supabase-green)
![AI](https://img.shields.io/badge/AI-Muzzle%20Biometrics-orange)

---

## 📌 Problem Statement

Livestock theft and ownership disputes are significant challenges in agriculture worldwide, affecting small and medium-scale farmers most severely.

**Traditional identification methods have critical limitations:**
- **Ear tags & bands** — Can be removed or replaced
- **Branding** — Can be altered or obscured
- **Paper records** — Easily lost, forged, or damaged
- **No digital verification** — Difficult to prove ownership in disputes

Our solution uses **biometric muzzle recognition** to create an immutable, unique digital identity for each animal that cannot be altered or duplicated.

---

## 💡 Solution

**Livestock Guardian** provides farmers and livestock owners with:

- **Quick Registration** — Add animals to your inventory with basic details
- **Biometric Capture** — Photograph the animal's muzzle (highly distinctive pattern)
- **AI-Powered Identification** — Instantly identify animals from new photos
- **Ownership Verification** — Prove ownership with cryptographic proof
- **Theft Reporting** — Flag stolen animals in the system
- **Complete History** — Maintain detailed ownership and health records
- **Cloud Sync** — Access your livestock data across devices
- **Searchable Database** — Find animals by various criteria  

---

## ⚙️ Tech Stack

| Component | Technology |
|-----------|------------|
| **Mobile App** | Kotlin/Java (Android SDK) |
| **Backend API** | FastAPI (Python) |
| **Database** | Supabase (PostgreSQL) |
| **Authentication** | Supabase Auth |
| **AI Engine** | Computer Vision (Muzzle Recognition) |
| **Cloud Hosting** | Supabase Cloud |
| **Image Storage** | Supabase Storage |

---

## 🎯 Key Features

### 📱 Mobile Features
- **Real-time Muzzle Recognition** — AI-powered identification using photos
- **Offline Mode** — Works without internet connection
- **Multi-Animal Dashboard** — Manage entire livestock inventory from one app
- **Photo Gallery Integration** — Capture or upload muzzle photos

### 🔒 Security & Verification
- **Cryptographic Proof of Ownership** — Unforgeable ownership certificates
- **Blockchain-ready** — Foundation for immutable ledger integration
- **Encrypted Storage** — All sensitive data encrypted end-to-end
- **Audit Trail** — Complete history of all transactions and changes

### ☁️ Cloud Capabilities
- **Real-time Sync** — Automatic backup across all devices
- **Theft Alert System** — Instant notifications for flagged animals
- **Cross-device Access** — Seamless experience on phone, tablet, and web
- **Export & Reporting** — Generate reports for authorities or insurance

### 🐄 Livestock Management
- **Health Tracking** — Log vaccinations, treatments, and medical history
- **Breed & Genealogy** — Maintain family trees and breed records
- **Location History** — Track where animals have been
- **Custom Tags** — Add custom fields for your farm's needs

---

## 📸 Screenshots

Screenshots will be added as the app UI and core features are finalized.

---

## 🔐 Core Technology: Muzzle Biometrics

Each animal possesses a **unique muzzle pattern** that is:

- **Distinctive** — No two animals have identical muzzle patterns
- **Stable** — Patterns remain consistent throughout the animal's life
- **Difficult to Forge** — Cannot be easily replicated or altered
- **Non-invasive** — Simple photograph capture, no special equipment needed
- **Universally Applicable** — Works across all livestock species (cattle, sheep, goats, etc.)

This biometric approach provides superior security compared to traditional identification methods and forms the foundation of the ownership verification system.

---

## 📦 Installation & Setup

### Prerequisites
- **Android Device**: Android 8.0+ (for mobile app)
- **Development Tools**: 
  - Android Studio 2022.1+ (for building from source)
  - Java JDK 11 or higher
- **Backend Requirements**:
  - Python 3.8+
  - Docker (recommended for backend deployment)
- **Cloud Account**: Free Supabase account (https://supabase.com)

### 1️⃣ Mobile App Setup

#### Option A: Download Pre-built APK
1. Go to the `apk/` directory in this repository
2. Download the latest `.apk` file
3. Transfer to your Android device
4. Open file manager and tap the APK to install
5. Grant necessary permissions (Camera, Storage, Contacts)

#### Option B: Build from Source
1. Clone this repository: `git clone https://github.com/yourusername/Livestock-Guardian.git`
2. Open the `app/LivestockGuardian/` directory in Android Studio
3. Sync Gradle files: **File > Sync Now**
4. Configure `local.properties` with your Android SDK path
5. Build the app: **Build > Build Bundle(s) / APK(s) > Build APK(s)**
6. Find the APK in `app/build/outputs/apk/`

### 2️⃣ Backend Setup

Detailed backend setup instructions are available in the backend repository or `DEVELOPMENT.md`.

Quick start:
```bash
# Clone backend repository
git clone https://github.com/yourusername/Livestock-Guardian-Backend.git
cd Livestock-Guardian-Backend

# Install dependencies
pip install -r requirements.txt

# Configure environment variables
cp .env.example .env

# Run development server
uvicorn main:app --reload
```

### 3️⃣ Supabase Configuration

1. Create a Supabase account at https://supabase.com
2. Create a new project
3. Get your API credentials from **Settings > API**
4. Configure these in your app's `local.properties` or environment variables:
   ```
   SUPABASE_URL=your_project_url
   SUPABASE_ANON_KEY=your_anonymous_key
   ```

### Getting Started
1. Clone this repository
2. Follow the setup instructions for mobile app and backend
3. Configure Supabase credentials
4. Launch the app on your device
5. Create an account or login
6. Register your first animal and capture muzzle biometrics
7. Upload to cloud and start tracking

---

## 🤝 Contributing

We welcome contributions! Please feel free to:
- Report bugs and issues
- Suggest new features
- Submit pull requests
- Improve documentation

---

## 📞 Support & Contact

For questions, feedback, or support, please open an issue in the repository.

---

## 📄 License

This project is released under the **MIT License**. See [LICENSE.txt](LICENSE.txt) for full details.
