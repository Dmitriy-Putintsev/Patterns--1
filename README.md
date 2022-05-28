[![Build status](https://ci.appveyor.com/api/projects/status/bpa3mfndu73w9ruo?svg=true)](https://ci.appveyor.com/project/Dmitriy-Putintsev/patterns-1)



# Задача №1 - "Проснулись" (Allure)

Взять ваш проект (см. "Как сдавать задачи") и прикрутить туда Allure, интегрированный с Selenide. 
Удостоверьтесь, что при локальном запуске всё работает, отчёты генерируются, скриншоты прикрепляются и вы можете их посмотреть через Allure.

## Инструкция по интеграции и настройке связки Allure-Selenide

1. В build.gradle прописать:
 ```javascript
plugins {
	...
	id 'io.qameta.allure' version '2.9.6'
}

...

allure {
	version = '2.17.2'
	//Если используется JUnit5, то добавляется:
	useJUnit5 { version = '2.17.2' }
	//версия должна совпадать с версией allure
}

repositories {
	mavenCentral()
}

dependencies {
	...

	testImplementation 'io.qameta.allure:allure-selenide:2.17.2'

	...
}
```
2. Включить в тестовый класс методы:
 ```javascript
@BeforeAll
public static void setUpAll() {
    SelenideLogger.addListener("allure", new AllureSelenide());
}
@AfterAll
public static void tearDownAll() {
    SelenideLogger.removeListener("allure");
}
 ```
В Intellij IDEA перейти во вкладку Terminal (Alt+F12) и запустить SUT командой
```javascript
java -jar artifacts/app-card-delivery.jar
```
Запустить авто-тесты В Intellij IDEA во вкладке Terminal открыв еще одну сессию, ввести команду
```javascript
./gradlew clean test allureReport
```
В Intellij IDEA во вкладке Terminal ввести команду
```javascript
./gradlew allureServe
```
