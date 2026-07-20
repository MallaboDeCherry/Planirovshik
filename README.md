📋Планы на год beta— Универсальный планировщик целей и задач
📖 О проекте
Планы на Год — это Android-приложение для долгосрочного планирования целей и задач. Оно позволяет создавать планы на год, отслеживать прогресс выполнения, вести историю записей и управлять подзадачами.

Приложение разработано на Java с использованием современных библиотек Android и архитектурных паттернов. Оно подходит для планирования любых жизненных целей: от покупки акций до чтения книг и спортивных достижений.

🎯 Основные функции
📋 Управление планами
Создание планов с указанием названия, описания, категории, срока и цвета

Числовая цель (необязательно): можно задать целевое значение и единицу измерения (например, "100 акций")

Простая задача (без числовой цели): например, "Сходить за продуктами"

Категории: Личное, Работа, Финансы, Покупки, Здоровье, Обучение, Путешествия, Творчество, Спорт, Другое

Фильтрация по категориям с помощью чипов

📊 Отслеживание прогресса
Круговой индикатор прогресса на главном экране

Детальный экран с полной информацией о плане

Добавление записей прогресса с указанием количества, даты, цвета и комментария

История записей с отображением всех изменений

Автоматическое вычисление процента выполнения

✅ Управление завершёнными целями
Полка завершённых целей — визуальное разделение активных и выполненных планов

Галочка ✅ при достижении 100% выполнения

Зелёный цвет для завершённых целей

📁 Архивация
Архивация планов через диалог подтверждения

Режим просмотра архива в главном меню

Восстановление планов из архива

📝 Подзадачи
Добавление подзадач внутри плана

Отметка выполнения подзадач через чекбокс

Удаление подзадач по долгому нажатию

🎨 Визуальное оформление
Светлая и тёмная тема (в разработке)

Выбор цвета для каждой карточки плана

Анимированные переходы

Material Design компоненты

🛠️ Технологии
Компонент	Технология
Язык	Java
База данных	Room (SQLite)
Архитектура	MVVM (Model-View-ViewModel)
Компоненты UI	Material Design, RecyclerView, ChipGroup, CircularProgressIndicator
Асинхронность	ExecutorService
Навигация	Intent, Activity
Диалоги	DialogFragment
📁 Структура проекта
text
com/example/planinagod/
├── data/                           # Слой данных
│   ├── AppDatabase.java           # База данных Room
│   ├── DateConverter.java         # Конвертер дат
│   ├── Plan.java                  # Сущность "План"
│   ├── PlanDao.java               # Запросы для планов
│   ├── ProgressRecord.java        # Сущность "Запись прогресса"
│   ├── ProgressRecordDao.java     # Запросы для записей
│   ├── Subtask.java               # Сущность "Подзадача"
│   └── SubtaskDao.java            # Запросы для подзадач
│
├── viewmodel/                      # Слой логики (ViewModel)
│   ├── MainViewModel.java         # Для главного экрана
│   └── PlanDetailViewModel.java   # Для деталей плана
│
└── ui/                             # Слой интерфейса
    ├── MainActivity.java          # Главный экран
    ├── PlanDetailActivity.java    # Экран деталей
    ├── SettingsActivity.java      # Настройки
    ├── adapters/
    │   ├── PlanAdapter.java       # Адаптер для планов
    │   └── HistoryAdapter.java    # Адаптер для истории
    └── dialogs/
        ├── AddPlanDialog.java     # Диалог создания плана
        ├── AddProgressDialog.java # Диалог добавления прогресса
        └── ArchiveConfirmDialog.java # Диалог подтверждения архивации
📱 Интерфейс
Главный экран
Toolbar с названием "Мои планы"

Чипы категорий для фильтрации (Все, Личное, Работа, Финансы, Покупки и др.)

Список планов с круговым индикатором прогресса

Полка завершённых целей внизу списка

FAB-кнопка для добавления нового плана

Экран деталей
Название плана

Круговой индикатор с процентом выполнения

Статистика: Выполнено / Осталось

Список подзадач с добавлением

Кнопка "+ Добавить запись"

История записей

Меню
Архив / Активные — переключение режима отображения

Настройки — выбор темы оформления

🚀 Как запустить проект
Требования
Android Studio (последняя версия)

JDK 11 или выше

Android SDK (API 24+)

Шаги
Клонируйте репозиторий:

bash
git clone https://github.com/yourusername/Planinagod.git
Откройте проект в Android Studio

Синхронизируйте Gradle:

text
File → Sync Project with Gradle Files
Запустите приложение на эмуляторе или реальном устройстве:

text
Run → Run 'app'
📦 Зависимости
gradle
dependencies {
    // Core Android
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.activity:activity:1.8.0'
    
    // Room
    implementation 'androidx.room:room-runtime:2.6.0'
    annotationProcessor 'androidx.room:room-compiler:2.6.0'
    
    // Lifecycle
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0'
    
    // RecyclerView
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    
    // Анимации
    implementation 'androidx.dynamicanimation:dynamicanimation:1.0.0'
}
🔄 Версии базы данных
Версия	Изменения
1	Создание таблиц plans и notes
2	Замена notes на progress_records
3	Добавление поля isArchived в Plan
4	Добавление таблицы subtasks
5	Добавление полей hasNumericGoal и isArchived
📝 Лицензия
Этот проект распространяется под лицензией MIT. Вы можете свободно использовать, модифицировать и распространять его.

