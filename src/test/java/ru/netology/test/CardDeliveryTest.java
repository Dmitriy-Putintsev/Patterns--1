package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.conditions.ExactText;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.datagenerator.DataGenerator;
import com.codeborne.selenide.Condition;
import org.openqa.selenium.Keys;


import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;


class CardDeliveryTest {
    DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser("Ru");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }


    @Test
    void shouldSuccessfullySendARequestToTheCard() {
        Configuration.holdBrowserOpen = true;

        //Заполнение и первоначальная отправка формы:
        $("[data-test-id='city'] input").setValue(userInfo.getCity());
        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String scheduledDate = DataGenerator.generateDate(4);
        $("[data-test-id='date'] input").setValue(scheduledDate);
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + scheduledDate), Duration.ofSeconds(15));

        //Изменение ранее введенной даты и отправка формы:
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String shiftDate = DataGenerator.generateDate(12);
        $("[data-test-id='date'] input").setValue(shiftDate);
        $(byText("Запланировать")).click();

        //Взаимодействие с опцией перепланировки,
        //а содержание текста и время загрузки:
        $("[data-test-id= replan-notification]")
                .shouldHave(Condition.text("Необходимо подтверждение " +
                        "У вас уже запланирована встреча на другую дату. Перепланировать?"), Duration.ofSeconds(15));
        $("[data-test-id=replan-notification] .button")
                .shouldHave(Condition.text("Перепланировать")).click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + shiftDate), Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("Should get error message if entered wrong phone number")
    void shouldGetErrorIfWrongPhone() {
        Configuration.holdBrowserOpen = true;

        $("[data-test-id='city'] input").setValue(userInfo.getCity());
        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String scheduledDate = DataGenerator.generateDate(4);
        $("[data-test-id='date'] input").setValue(scheduledDate);
        $("[data-test-id='phone'] input").setValue(DataGenerator.generatePhone("en"));
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();
        $("[data-test-id='phone'] input_sub")
                .shouldHave(new ExactText("Неверный формат номера мобильного телефона"));
    }
}
