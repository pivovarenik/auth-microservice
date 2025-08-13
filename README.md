Small library API
Проект реализует связь с базой данных mysql с помощью docker-контейнера и реализует CRUD операции с книгами

Как запустить приложение
Создайте любую папку на компьютере и внутри нее введите команду

  git clone https://github.com/pivovarenik/Library-Spring-Boot-API.git
После этого должна создаться папка с таким же названием Перейдите в нее

  cd Library-Spring-Boot-API
Запуск 2 серверов book-storage-service и book-tracker-service
Запустите 2 командные строки и перейдите в

    cd book-storage-service
Запустите Spring-boot server в двух приложениях

    mvn spring-boot:run
Затем перейдите в

   cd book_tracker_service
и повторите команду

Требования
1. Установленный docker на устройстве
2. Свободные порты 3307, 22181 и 29092
3. Проверить какие порты на устройстве заняты можно с помощью команды netstat -aon | findstr ":порт который вам нужен"
4. Если порты заняты: либо освободите порты либо измените конфигурацию compose.yaml файла
Проверить работу приложения можно с помощью файла

postman-collection-for-apis.yaml
который находится в папке

book-storage-service/api
Тестирование
Есть один запрос на который стоит проверка bearer-token

post(http://localhost:8080/api/library)
В нем обязательно нужно добавить authorization token который можно получить с помощью

post(localhost:8080/register)
post(localhost:8080/generate-token)
В первом запросе передается username и password нового пользователя а во втором после регистрации получается token который можно использовать при первом запросе
