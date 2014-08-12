## AccountService

### Сервис 

Сервис предоставляет два метода: 

* getAmount(id) - Возвращает баланс переданного идентификатора
* addAmount(id, amount) - Изменяет баланс переданного идентификатора на указанное значение

Для управлением сервера доступны три команды (вводятся в консоль):

* show statistics - показать статистику
* reset statistics - сбросить статистику
* shutdown - выключить сервер

### Тестовый клиент

Запускает несколько потоков, вызывающих методы getAmount и addAmount сервера. 
Работает на заданном множестве идентификаторов. 
Количество потоков и идентификаторы задаются через агрументы командной строки.

Для управления клиентом существует только одна команда:

* shutdown - завершить работу клиента

### Компиляция (пример для linux)

javac -d bin src/ru/kapahgaiii/config/\*.java  
javac -d bin -cp ./bin src/ru/kapahgaiii/server/\*.java  
javac -d bin -cp ./bin:./lib/args4j-2.0.21.jar src/ru/kapahgaiii/client/\*.java

### Запуск 

Точка входа сервера : ru.kapahgaiii.server.Service  
Точка входа клиента : ru.kapahgaiii.client.Client

Клиент принимает следующие агрументы команой строки:

* -rCount - количество потоков, вызывающих метод getAmount 
* -wCount - количество потоков, вызывающих метод addAmount
* -idList - диапазон ключей. Через зяпятую числа или интервалы. Пример: "1,2,5-7,4,81-99,70"

#### Пример запуска сервера

java -cp ./bin:/usr/share/java/mysql.jar ru.kapahgaiii.server.Service

#### Пример запуска клиента

java -cp ./bin:./lib/args4j-2.0.21.jar ru.kapahgaiii.client.Client -rCount 2 -wCount 3 -idList 1-30,50,70-200
