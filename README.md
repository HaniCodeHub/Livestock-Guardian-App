# Livestock Guardian 🐄

![Livestock Guardian](screenshorts/0_livestock_guardian_logo.png)

AI-Powered Livestock Identification & Theft Prevention System

An Android application that uses biometric muzzle recognition, artificial intelligence, and cloud technologies to uniquely identify livestock, verify ownership, and reduce animal theft.

This project is actively being improved for practical farm and livestock management use by combining smart recognition with real-world livestock operations.

The repository is maintained with regular documentation updates to support both new users and contributors.

It aims to support safer animal tracking and ownership verification in real-world agricultural settings.

The app is designed to be practical, scalable, and useful for modern livestock management workflows.

This README is being refined to make the project easier to understand for both farmers and developers.

Key goals of the project:
- Make livestock identification faster and more reliable
- Reduce theft and ownership disputes with verifiable records
- Provide a scalable foundation for real-world farm operations

How the system works:
- Farmers register animals with basic details and a muzzle photo
- The AI model creates a biometric profile for each animal
- New photos can be compared against stored profiles for quick identification
- Verified records can support ownership proof, tracking, and theft reporting

> Project status: the app and documentation are being actively refined to make deployment, onboarding, and contribution easier for the community.

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Backend](https://img.shields.io/badge/Backend-FastAPI-blue)
![Database](https://img.shields.io/badge/Database-Supabase-green)
![AI](https://img.shields.io/badge/AI-Muzzle%20Biometrics-orange)

---

## ⚡ Quick Start (5 minutes)

**New users?** Get started in just a few steps:

This section is being kept concise so the main setup flow remains easy to follow.

> Tip: if you are testing the app for the first time, start with the demo flow before registering real animals.

1. **Download the App**
   - Get the latest APK from the `apk/` folder or build from source in `app/`

2. **Install & Open**
   - Install on your Android device (8.0+)
   - Grant camera and storage permissions when prompted

3. **Create Account**
   - Sign up with email or use demo mode
   - Set up your farm profile

4. **Register Your First Animal**
   - Tap "Add Animal" and enter basic info (name, breed, etc.)
   - Take a clear photo of the animal's **muzzle** (nose/mouth area)
   - The AI analyzes and creates a unique biometric identity

5. **Access Anywhere**
   - Your data syncs automatically to the cloud
   - Access from web dashboard or mobile app
   - Identify animals instantly with new photos

➡️ **For detailed setup instructions**, see [Installation & Setup](#-installation--setup)

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

- **Practical Farm Workflow Support** — Designed for daily livestock operations, not just experimental demos

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


### Project Purpose
This project is intended to support modern livestock operations with practical, affordable tools for animal identification and verification.


### Who It Helps
The app is designed for farmers, livestock owners, cooperatives, and organizations that need a dependable way to track and verify animals.


### Why It Matters
Reliable animal records can reduce disputes, improve traceability, and strengthen confidence in farm operations.

## 📚 Documentation

Explore our comprehensive documentation:

> The documentation set is being expanded regularly to support both end users and contributors.

| Documentation | Purpose |
|---------------|---------|
| [API_DOCUMENTATION.md](API_DOCUMENTATION.md) | Complete API endpoints and usage |
| [ARCHITECTURE.md](ARCHITECTURE.md) | System design and architecture overview |
| [DEVELOPMENT.md](DEVELOPMENT.md) | Developer setup and workflow |
| [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) | Database structure and relationships |
| [BACKEND_GUIDELINES.md](BACKEND_GUIDELINES.md) | Backend code standards and practices |
| [ANDROID_GUIDELINES.md](ANDROID_GUIDELINES.md) | Mobile development standards |
| [DEPLOYMENT.md](DEPLOYMENT.md) | Production deployment guide |
| [SECURITY.md](SECURITY.md) | Security best practices and threat model |
| [TESTING.md](TESTING.md) | Testing strategy and guidelines |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | Common issues and solutions |
| [ML_DEVELOPMENT.md](ML_DEVELOPMENT.md) | Machine learning model development |
| [PERFORMANCE.md](PERFORMANCE.md) | Performance optimization tips |
| [USER_GUIDE.md](USER_GUIDE.md) | End-user documentation |

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
1. Clone this repository: `git clone https://github.com/HaniCodeHub/Livestock-Guardian.git`
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
git clone https://github.com/HaniCodeHub/Livestock-Guardian-Backend.git
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

## 🤝 Contributing & Development

We welcome contributions from the community! Here's how you can help:

### 🐛 Report Issues
- Found a bug? Please [open an issue](https://github.com/HaniCodeHub/Livestock-Guardian/issues/new?assignees=&labels=bug&template=bug_report.md&title=%5BBUG%5D+) with:
  - Detailed description of the problem
  - Steps to reproduce
  - Device/OS information
  - Screenshots or logs if applicable

### ✨ Suggest Features
- Have an idea? [create a feature request](https://github.com/HaniCodeHub/Livestock-Guardian/issues/new?assignees=&labels=enhancement&template=feature_request.md&title=%5BFEATURE%5D+) using the template
- Explain the use case and expected behavior
- Reference related issues or PRs if applicable

### 💻 Submit Code

#### Setup Development Environment
1. Fork the repository
2. Clone your fork: `git clone https://github.com/HaniCodeHub/Livestock-Guardian.git`
3. Create a feature branch: `git checkout -b feature/your-feature-name`
4. Set up your development environment:
   - For mobile: Follow [ANDROID_GUIDELINES.md](ANDROID_GUIDELINES.md)
   - For backend: Follow [DEVELOPMENT.md](DEVELOPMENT.md)

#### Coding Standards
- Follow the guidelines in [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- Mobile code: See [ANDROID_GUIDELINES.md](ANDROID_GUIDELINES.md)
- Backend code: See [BACKEND_GUIDELINES.md](BACKEND_GUIDELINES.md)
- Documentation: See [DOCUMENTATION_STANDARDS.md](DOCUMENTATION_STANDARDS.md)

#### Testing & Quality
- Write tests for your changes (see [TESTING.md](TESTING.md))
- Ensure all tests pass: `./gradlew test` (mobile) or `pytest` (backend)
- Run linters and fix any issues before submitting

#### Submit a Pull Request
1. Commit with clear, descriptive messages
2. Push to your fork
3. Open a PR with detailed description of changes
4. Link any related issues: "Fixes #123"
5. Wait for code review and feedback
6. Make requested changes and push updates

### 🎓 Developer Resources
- [ARCHITECTURE.md](ARCHITECTURE.md) — Understand the system design
- [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) — Learn the data model
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) — API reference
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) — Common issues
- [PERFORMANCE.md](PERFORMANCE.md) — Optimization techniques

### 📝 Improve Documentation
- Spotted a typo or unclear docs? Submit a PR
- Help translate documentation to other languages
- Improve code examples and tutorials

---

## 📞 Support & Contact

For questions, feedback, or support, please open an issue in the repository.

---

## 📄 License

This project is released under the **MIT License**. See [LICENSE.txt](LICENSE.txt) for full details.
