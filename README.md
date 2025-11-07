<div align="center">

# 🇰🇷✨ Korean Verbs AI

### Master Korean Verbs with AI-Powered Learning

[![Google Play](https://img.shields.io/badge/Google%20Play-Download-green?style=for-the-badge&logo=google-play)](https://play.google.com/store/apps/details?id=com.kreggscode.koreanverbs)
[![Website](https://img.shields.io/badge/Website-Orchid%20AI-purple?style=for-the-badge&logo=google-chrome)](https://kreggscode.github.io/Korean-Verbs-Ai/)
[![Privacy Policy](https://img.shields.io/badge/Privacy-Policy-orange?style=for-the-badge&logo=readme)](https://kreggscode.github.io/Korean-Verbs-Ai/privacy.html)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-blue?style=for-the-badge&logo=github)](https://github.com/kreggscode/Korean-Verbs-Ai)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

A production-grade Android application for learning Korean with AI assistance, premium glassmorphic UI, and cultural immersion content.

[Download on Google Play](https://play.google.com/store/apps/details?id=com.kreggscode.koreanverbs) • [Official Website](https://kreggscode.github.io/Korean-Verbs-Ai/) • [Privacy Policy](https://kreggscode.github.io/Korean-Verbs-Ai/privacy.html) • [Report Bug](https://github.com/kreggscode/Korean-Verbs-Ai/issues)

</div>

---

## 🎨 Features

### **Core Features**
- 📚 **Comprehensive Verb Database**: 1,898+ curated verbs with Hangul, romanization, English meanings, and usage notes
- 🤖 **AI Chat Assistant**: Real-time tutoring powered by Pollinations AI with explanation, conjugation, and cultural context support
- 📱 **Premium Glassmorphic Design**: Responsive layout, adaptive themes, and motion-rich interactions
- 🎯 **Interactive Quizzes**: Multiple difficulty modes, flashcards, time challenges, and progress analytics
- 🔤 **Hangul Learning**: Alphabet explorer with pronunciation audio and practice tips
- 🍲 **Korean Culture & Cuisine Hubs**: Encyclopedia-style tabs covering festivals, etiquette, popular dishes, and more
- 📘 **Basic Grammar Guide**: Honorifics, particles, verb endings, and tense breakdowns in approachable modules
- 📸 **Object & Text Scanner**: ML Kit powered recognition with 16 KB aligned native binaries for Android 15+ devices
- 🔊 **Text-to-Speech Everywhere**: Native audio for Hangul and English across the app

### **Screens & Modules**
1. **Home Dashboard** – Daily streaks, verb stats, quick actions to Grammar/Culture/Cuisine modules
2. **Verbs Explorer** – Category filters, search, favorites, offline caching for 16 KB devices
3. **Verb Detail** – TTS, conjugation tables, AI explanation card, previous/next navigation carousel
4. **Hangul Studio** – Tabs for vowels, consonants, pronunciation guides, custom animations
5. **Quiz Lab** – Flashcards, MCQs, time attack, adaptive difficulty, performance summaries
6. **AI Tutor** – Conversational assistant with history, knowledge cut-off awareness, and voice reply
7. **Culture Encyclopedia** – Overview, festivals, traditions, etiquette, facts in glassmorphic cards
8. **Cuisine Guide** – Popular, famous, regular dishes plus likes/dislikes insight tabs
9. **Basic Grammar** – Basics, particles, verb endings, honorifics, tenses with examples
10. **Scanner Studio** – ML Kit image labeling and OCR with safety timeouts and retry logic

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer
- Android SDK 36 (Android 16)
- Kotlin 1.9.20 (bundled with AGP 8.13.0)
- Java 17 (JDK 17+)

### Installation

1. **Clone the repository**
```bash
git clone [repository-url]
cd "Korean verbs AI"
```

2. **Open in Android Studio**
- Open Android Studio
- Select "Open an Existing Project"
- Navigate to the project directory

3. **Build the project**
- Let Gradle sync complete (AGP 8.13.0)
- Build > Make Project (or `./gradlew assembleDebug`)

4. **Run the app**
- Connect Android device or start emulator
- Click Run button or press Shift+F10

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin (JVM 17 target)
- **UI**: Jetpack Compose Material 3 + custom glassmorphism kit
- **Architecture**: MVVM + modular repositories
- **Networking / AI**: Ktor Client + Pollinations AI helpers
- **Persistence**: DataStore + cached JSON assets
- **Vision**: ML Kit (text recognition 16.0.1, image labeling 17.0.9) with 16 KB aligned binaries
- **Camera**: CameraX 1.4.0 pipeline
- **Animations**: Compose animation APIs & physics-based transitions

### Project Structure
```
app/
├── src/main/
│   ├── java/com/kreggscode/koreanverbs/
│   │   ├── data/
│   │   │   ├── models/
│   │   │   ├── repository/
│   │   │   └── ai/
│   │   ├── navigation/
│   │   ├── ui/
│   │   │   ├── screens/
│   │   │   ├── components/
│   │   │   └── theme/
│   │   ├── MainActivity.kt
│   │   └── KoreanVerbsApplication.kt
│   ├── res/
│   └── assets/
│       └── korean_verbs.json
└── build.gradle.kts
```

## 🎨 Design System

### Color Palette
- **Primary**: Premium Indigo (#6366F1)
- **Secondary**: Premium Purple (#8B5CF6)
- **Accent**: Premium Pink (#EC4899)
- **Support**: Teal (#14B8A6), Emerald (#10B981), Amber (#F59E0B)
- **Glassmorphic Effects**: White/Black overlays with 0.03–0.15 alpha

### Components
- `GlassmorphicCard`: Frosted surfaces with dynamic blur
- `AnimatedGradientButton`: Gradient shimmer CTA buttons
- `PremiumCard`: Elevated cards with soft shadows
- `PulsingIcon`: Breathing icon animation for attention cues
- `AnimatedProgressBar`: Smooth loading states and quiz timers
- `FloatingNavigationBar`: Edge-to-edge glass navigation with system bar padding

## 📱 Screenshots

The app features:
- Animated splash screen with Korean flag inspiration
- Edge-to-edge design with transparent system bars
- Smooth page transitions (400ms slide + fade)
- Spring physics button animations
- Gradient overlays on primary actions

## 🔧 Configuration

### API Keys
The app uses Pollinations AI which doesn't require API keys for basic usage.

### Permissions Required
- `INTERNET` – AI explanations, culture/grammar updates
- `CAMERA` – Scanner and OCR features
- `VIBRATE` – Haptic guidance

## 🚦 Building for Production

1. **Generate signed App Bundle**
   - `Build > Generate Signed Bundle / APK > Android App Bundle`
2. **R8 & ProGuard**
   - Rules are pre-configured (`proguard-rules.pro`, ML Kit keep rules included)
3. **Versioning**
   - Update `versionCode` / `versionName` in `app/build.gradle.kts`
4. **16 KB Compliance**
   - ML Kit native libs updated to 16 KB aligned variants (verified with `llvm-objdump`)
   - Build with `./gradlew clean bundleRelease`

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- **Pollinations AI**: For providing free AI API
- **ML Kit**: For vision capabilities
- **Korean Verbs Dataset**: Community contributed data
- **Material Design 3**: For design guidelines

## 📞 Support & Contact

- **Email**: kreg9da@gmail.com
- **Developer**: KreggsCode
- **Website**: [Official Site](https://kreggscode.github.io/Korean-Verbs-Ai/)
- **Privacy Policy**: [https://kreggscode.github.io/Korean-Verbs-Ai/privacy.html](https://kreggscode.github.io/Korean-Verbs-Ai/privacy.html)
- **Issues**: [GitHub Issues](https://github.com/kreggscode/Korean-Verbs-Ai/issues)
- **Documentation**: [Project Docs](https://kreggscode.github.io/Korean-Verbs-Ai/)

---

**Package Name**: `com.kreggscode.koreanverbs`  
**Min SDK**: 24 (Android 7.0)  
**Target SDK**: 36 (Android 16)
