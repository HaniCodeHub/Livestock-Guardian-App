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
- Android 8.0+ device
- Supabase account (free tier available)
- Backend service running (FastAPI)

### Mobile App Setup
Detailed setup instructions for building and installing the Android app are in the `app/` directory.

### Backend Setup
FastAPI backend configuration and deployment instructions are documented in the backend repository.

### Getting Started
1. Clone this repository
2. Install dependencies as per the README in each module
3. Configure Supabase credentials
4. Build the Android app or start the backend service
5. Register your first animal and capture muzzle biometrics

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
