# Commission Task - Star Wars Characters

## Variant Code
- Лисицына Алёна Алексеевна
-  Б9123-09.03.03 ПИКД
- `SWAPI-PEOPLE-MOD_A1_STICKY_HEADERS`

## Используемые endpoints
- **List**: `https://swapi.dev/api/people/?page={page}` - получение списка персонажей (первые 2 страницы)
- **Search**: `https://swapi.dev/api/people/?search={query}` - поиск персонажей по имени
- **Detail**: `https://swapi.dev/api/people/{id}/` - получение детальной информации о персонаже

## Реализованный модификатор: MOD_A1_STICKY_HEADERS
Список персонажей сгруппирован по первой букве имени и имеет "липкие" заголовки (sticky headers).
При прокрутке списка заголовок группы (буква) остается в верхней части экрана, пока все элементы этой группы не будут прокручены.


