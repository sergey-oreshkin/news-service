# Пример работы с пакетом Rome для парсинга rss лент  

#### Первый этап
Нужно создать API для веб клиента.  
Клиент для локальной разработки можно взять [вот тут](https://github.com/sergey-oreshkin/front-news-agregator).

Либо просто перейти [на эту страницу](https://sergey-oreshkin.github.io/news-searcher-react/)
и при запущенном на вашей машине сервере всё будет работать. Все последующие этапы будут работать только с клиентом по этой ссылке.   

Клиент обращается к адресу 'http://localhost:8080/'. Адрес можно поменять в первой строке js.js файла.

Он отправляет POST запрос в json формате с двумя полями:
- int hours
- List\<String>  keywords

и должен получить в ответе новости за последние столько часов сколько указано в hours  
и содержащие в заголовке или описании ключевые слова из списка  
Ответ должен быть в формате json и содержать следующие поля:
- String title (заголовок новости)
- String desc (описание новости)
- String link (ссылку на новость)
- Date date (дата публикации)

Загружать RSS каждый раз когда сервер получает запрос от клиента - плохая идея. 
В реальности на настоящем рабочем сервисе их может быть по несколько в секунду, и,
хотелось бы отвечать на запросы быстро, чтоб и сервер меньше нагружался,
и клиент не ждал ответа по несколько секунд. Поэтому нужно кэширование новостей.   
Кэширование можно реализовать таким средством спринга как Scheduling. Почитать про это можно 
[тут](https://habr.com/ru/post/580062/), или из других источников.   
Идея в том, чтобы опрашивать RSS источники в отдельном потоке напрмер каждые 5 минут и сохранять в джава переменные,
а клиенту отдавать отфильтрованные новости из этих переменных, не нагружая сервер каджый раз.   

Так же не забывайте обрабатывать все ошибки которые случаются при запросах или парсинге RSS. 
В начальный набор RSS источников специально включены такие, которые вызывают различные ошибки и 
это не должно быть проблемой.


#### Важно!
Для корректной работы локального фронтенда над конроллером нужна аннотация
    `@CrossOrigin(origins = "*")`

#### Как проверить
Проверить работу готового приложения можно локально запустив свое приложение 
и открыв файл index.html скачанного клиента в своем браузере.
Или [страницу клиента](https://sergey-oreshkin.github.io/news-searcher-react/).
Ну и конечно нужно написать свои тесты.

#### Планы дальнейшего развития
Планируется сделать регистрацию пользователя и предоставить возможность 
редактировать список rss источников, а так же подписываться на email рассылку новостей по указанным им параметрам

Присоединяйтесь, будет интересно :)

#### Следующие этапы 
Вторая часть - регистрация пользователя и авторизация c помощью jwt токена доступен в ветке
[jwt-auth](https://github.com/sergey-oreshkin/news-service/tree/jwt-auth).   
Третья часть - продвинутая работа с jwt токенами в ветке [jwt-auth-adv](https://github.com/sergey-oreshkin/news-service/tree/jwt-auth-adv)
