# Приложение на любом открытом API
## Домашнее задание 5 - Тестирование

- **ФИО:** Лисицына Алёна Алексеевна
- **Группа:** Б9123-09.03.03 ПИКД (1 подгруппа)
- **API: SWAPI** (Star Wars API) - информация о персонажах Star Wars
- **Как запустить:** просто собрать проект, API бесплатный без ключа
- Хотелось бы оценку 5, но сомневаюсь, что дотяну, поэтому стремлюсь к 4

## Выполненные тесты

### Юнит-тесты (8 шт)
Файл: `CharactersViewModelTest.kt`
- обновление query и очистка ошибки (`onQueryChange updates query and clear error`)
- очистка поиска загружает всех персонажей (`clear search query loads all characters`)
- успешная загрузка данных (`search success updates visibleCharacter and calls repository`)
- ошибка загрузки (`load characters error sets error message and stops loading`)
- корректное начальное состояние экрана (`initial state is correct`)
- retry после ошибки (`retry after error clears error and loads characters`)
- пустой результат поиска даёт Empty (`search with no results returns empty list`)
- повторное добавление в избранное не создаёт дубль (`adding same character to favourites does not create duplicate`)

### Интеграционные тесты (5 шт)
#### Data-слой
Файл: `FavoriteCharacterDaoTest.kt`
- после записи в Room данные корректно читаются повторно (`insert_AndGetAll_returnsItemsOrderedByAddedAtDesc`)

#### UI-тесты
Файл: `CharacterListScreenTest.kt`
- переход на детали открывает экран для нужного id (`click_on_character_navigates_to_detail_screen`)
- отображение корректного состояния экрана после загрузки данных (`after_loading_displays_character_list_correctly`)
- retry() инициирует новую попытку запроса (`retry_initiates_new_request_after_error`)
- состояние после ошибки и повторной загрузки переходит корректно (`state_transitions_correctly_from_error_to_success_after_retry`)

### Нетривиальные тесты (6 шт)
Из списка задания покрыты:
- повторное добавление в избранное не создаёт дубль
- пустой результат поиска даёт Empty
- переход на детали открывает экран для нужного id
- после записи в Room данные корректно читаются повторно
- retry() инициирует новую попытку запроса
- состояние после ошибки и повторной загрузки переходит корректно

## Скриншоты
### Экран загрузки
![img_4.png](img_4.png)

### Экран ошибки
![img_5.png](img_5.png)

### Экран со списком
![img_6.png](img_6.png)

### Экран с деталями
![img_1.png](img_1.png)

### Экран любимых
![img_2.png](img_2.png)

### Состояние Room
![img_7.png](img_7.png)