<div align="center">

# 🇰🇷✨ Korean Verbs AI

### Master Korean Verbs with AI-Powered Learning

[![Google Play](https://img.shields.io/badge/Google%20Play-Download-green?style=for-the-badge&logo=google-play)](https://play.google.com/store/apps/details?id=com.kreggscode.koreanverbs)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-blue?style=for-the-badge&logo=github)](https://github.com/kreggscode/Korean-Verbs-Ai)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

A visually stunning Android application for learning Korean verbs with AI assistance, featuring glassmorphic UI design and premium animations.

[Download on Google Play](https://play.google.com/store/apps/details?id=com.kreggscode.koreanverbs) • [View Documentation](https://kreggscode.github.io/Korean-Verbs-Ai/) • [Report Bug](https://github.com/kreggscode/Korean-Verbs-Ai/issues)

</div>

---

## 🎨 Features

### **Core Features**
- 📚 **Comprehensive Verb Database**: 1500+ Korean verbs with pronunciations and examples
- 🤖 **AI Chat Assistant**: Powered by Pollinations AI for personalized learning
- 📱 **Glassmorphic Design**: Premium UI with stunning animations and blur effects
- 🎯 **Interactive Quizzes**: Multiple choice, flashcards, and time challenges
- 🔤 **Hangul Learning**: Interactive tracing and pronunciation guide
- 📸 **Object Scanner**: Real-time object detection and Korean translation
- 🔊 **Text-to-Speech**: Native Korean and English pronunciation

### **Screens**
1. **Home Screen**: Quick stats, categories showcase, featured learning
2. **Verbs Screen**: Browse by category, search functionality
3. **Verb Detail**: Conjugations, examples, TTS support
4. **Hangul Screen**: Learn Korean alphabet with tracing
5. **Quiz Screen**: Multiple game modes with difficulty levels
6. **AI Chat**: Real-time Korean language assistance
7. **Scanner**: Camera-based object recognition

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 34
- Kotlin 1.9.20
- Java 17

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
- Let Gradle sync complete
- Build > Make Project

4. **Run the app**
- Connect Android device or start emulator
- Click Run button or press Shift+F10

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Manual (can add Hilt)
- **Network**: Ktor Client
- **AI Integration**: Pollinations AI API
- **ML Kit**: Text Recognition, Image Labeling
- **Camera**: CameraX
- **Animations**: Compose Animations + Lottie

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
- **Primary**: Indigo (#6366F1)
- **Secondary**: Purple (#8B5CF6)
- **Accent**: Pink (#EC4899)
- **Teal**: (#14B8A6)
- **Glassmorphic Effects**: White/Black with alpha

### Components
- `GlassmorphicCard`: Frosted glass effect cards
- `AnimatedGradientButton`: Gradient animated buttons
- `PremiumCard`: Elevated gradient border cards
- `PulsingIcon`: Breathing animation icons
- `AnimatedProgressBar`: Smooth progress indicators
- `FloatingNavigationBar`: Glass effect bottom nav

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
- `INTERNET`: For AI features and API calls
- `CAMERA`: For object scanner feature
- `VIBRATE`: For haptic feedback

## 🚦 Building for Production

1. **Generate signed APK**
```
Build > Generate Signed Bundle/APK
```

2. **Configure ProGuard**
Already configured in `build.gradle.kts`

3. **Set version**
Update in `app/build.gradle.kts`:
```kotlin
versionCode = 1
versionName = "1.0"
```

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
- **Issues**: [GitHub Issues](https://github.com/kreggscode/Korean-Verbs-Ai/issues)
- **Documentation**: [Project Docs](https://kreggscode.github.io/Korean-Verbs-Ai/)

---

**Package Name**: `com.kreggscode.koreanverbs`  
**Min SDK**: 24 (Android 7.0)  
**Target SDK**: 34 (Android 14)
