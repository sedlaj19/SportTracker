# SportTracker

## 📋 Zadání

### Úvod
Cílem zadání je vytvořit jednoduchou mobilní aplikaci, která bude zaznamenávat
sportovní výkony a ukládat je na backend (např. Firebase, vlastní řešení,...) a do
lokální databáze (např. Room, Realm, ...) dle volby uživatele.

Aplikace bude mít dvě obrazovky. První pro zadávání sportovních výkonů. Druhá
bude sloužit pro výpis již zadaných sportovních výkonů. Součástí úkolu je vymyslet
průchod aplikací, aby dával z uživatelského pohledu co největší smysl. Následně
vymyšlené navigační flow nejen implementovat, ale i vysvětlit, proč jsme zvolili
právě toto řešení. Aplikace musí správně fungovat jak v landscape, tak v portrait
módu. Snaž se použít jednotnou architekturu napříč celým projektem.

### Zadávání sportovního záznamu
Obrazovka pro zadávání sportovních výkonů bude implementovat následující
požadavky:
- Zadání názvu sportovního výkonu.
- Zadání místa konání sportovnímu výkonu.
- Zadání délky trvání sportovního výkonu.
- Výběr, zda položku uložit do lokální databáze nebo na backend.
- Uložení položky sportovního výkonu do vybraného úložiště.

### Výpis již zadaných sportovních výkonů
Obrazovka pro výpis sportovních výkonů obsahuje:
- Výpis vložených položek dle výběru (All | Local | Remote)
- Barevně odlišené položky dle typu úložiště

Jakýkoliv další rozvoj aplikace, kterým bys chtěl ukázat své schopnosti, je vítán.

## 📱 Popis aplikace

SportTracker je mobilní aplikace pro sledování sportovních aktivit postavená na moderních technologiích. Aplikace umožňuje zaznamenávání sportovních výkonů, jejich ukládání do lokální či vzdálené databáze a prohlížení s možností filtrování.

### Klíčové funkce
- 📝 Zaznamenávání sportovních aktivit (název, místo, doba trvání, typ aktivity)
- 💾 Podpora lokálního (Room) i vzdáleného úložiště (Firebase)
- 🔍 Filtrování aktivit podle typu úložiště
- 📱 Podpora portrait i landscape módu
- 🎨 Material 3 design s českou lokalizací

## 🛠 Technologie

### Core Framework
- **Kotlin Multiplatform (KMP)** - sdílená business logika napříč platformami
- **Compose Multiplatform** - moderní deklarativní UI framework
- **Kotlin** 2.2.20

### Architektura
- **MVVM + Clean Architecture** - oddělení presentation, domain a data vrstev
- **Dependency Injection**: Koin 4.1.1
- **Navigation**: Jetpack Navigation Compose

### Databáze & Backend
- **Room** 2.8.1 - lokální databáze s KSP procesorem
- **Firebase** - Firestore + Authentication pro vzdálené úložiště
- **Ktor** 3.3.0 - HTTP klient

### UI & UX
- **Material 3** - moderní Material Design
- **Coil** 3.3.0 - asynchronní načítání obrázků

### Testing
- **JUnit** + **Mockk** - unit testování
- **Espresso** - UI testování
- **Turbine** - testování Kotlin Flow
- **Coroutines Test** - testování asynchronního kódu

## 🏗 Struktura projektu

```
SportTracker/
├── shared/                    # Sdílená business logika (KMP)
│   ├── domain/               # Business pravidla a use cases
│   ├── data/                 # Repozitáře, databáze, API
│   └── presentation/         # ViewModels a presentation logika
├── composeApp/               # UI komponenty (Compose Multiplatform)
│   ├── screens/             # Obrazovky aplikace
│   └── components/          # Znovupoužitelné UI komponenty
└── androidApp/              # Android-specific kód
    ├── MainActivity.kt      # Vstupní bod aplikace
    └── Application.kt       # Inicializace DI
```

## 🧪 Testování

Projekt obsahuje kompletní testovací infrastrukturu:

- **Unit testy** - testování business logiky v `shared/src/commonTest/`
- **Instrumentované testy** - UI testy v `androidApp/src/androidTest/`
- **Repository testy** - testování datové vrstvy s Mockk

## 🔧 Konfigurace

### Build Variants
- **Debug** - pro vývoj s logováním
- **Release** - produkční verze s optimalizacemi
