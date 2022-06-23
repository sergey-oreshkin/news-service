# Пример работы с пакетом Rome для парсинга rss лент  
Форкните этот или создайте свой репозиторий для разработки проекта  
#### Первый этап
Нужно создать API для веб клиента.  
Клиент для локальной разработки можно взять [вот тут](https://github.com/sergey-oreshkin/front-news-agregator)  
Клиент обращается к адресу 'http://localhost:8080/'. Адрес можно поменять в первой строке js.js файла.   
Он отправляет POST запрос в json формате с двумя полями
- int hours
- List\<String>  

и должен получить в ответе новости за последние столько часов сколько указано в hours  
и содержащие в заголовке или описании ключевые слова из списка  
Ответ должен быть в формате json и содержать следующие поля
- String title (заголовок новости)
- String desc (описание новости)
- String link (ссылку на новость)
- Date date (дата публикации)

#### Важно!
Для корректной работы локального фронтенда над конроллером нужна аннотация

    @CrossOrigin(origins = "*")


#### Как проверить
Проверить работу готового приложения можно локально запустив свое приложениео 
и открыв файл index.html указанного выше клиента в своем браузере.
Ну и конечно нужно написать свои тесты.

#### Планы дальнейшего развития
Планируется сделать регистрацию пользователя и предоставить возможность 
редактировать список rss источников, а так же подписываться на email рассылку новостей по указанным им параметрам

Присоединяйтесь, будет интересно :)

#### Следующий этап 
Следующий этап регистрация пользователя и авторизация c помощью jwt токена доступен в ветке jwt-auth.