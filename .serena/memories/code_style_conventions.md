# SportTracker - Code Style & Conventions

## Naming Conventions
- **Classes**: PascalCase (SportActivity, ActivityListViewModel)
- **Functions/Variables**: camelCase (saveActivity, isLoading)
- **Constants**: UPPER_SNAKE_CASE (STORAGE_TYPE_LOCAL)
- **Packages**: lowercase with dots (com.sporttracker.domain.model)

## File Structure Patterns
- **Domain Layer**: `/domain/model/`, `/domain/usecase/`, `/domain/repository/`
- **Data Layer**: `/data/repository/`, `/data/datasource/`, `/data/local/`, `/data/remote/`
- **Presentation**: `/presentation/viewmodel/`, `/presentation/model/`
- **UI**: `/ui/screens/`, `/ui/components/`, `/ui/theme/`

## Architecture Patterns
- **MVVM**: ViewModels with UiState and Events
- **Repository Pattern**: Single source of truth
- **Clean Architecture**: Domain, Data, Presentation separation
- **Dependency Injection**: Koin modules (AppModule, AndroidModule)

## Code Style
- **Kotlin style**: Official Kotlin coding conventions
- **No comments policy**: Code should be self-documenting
- **Null safety**: Prefer non-null types, use safe calls
- **Coroutines**: Use structured concurrency with Flow
- **Compose**: Stateless composables with state hoisting

## Error Handling
- Use `Result<T>` for error-prone operations
- Sealed classes for UiState (Loading, Success, Error)
- Try-catch blocks in data layer, not UI layer