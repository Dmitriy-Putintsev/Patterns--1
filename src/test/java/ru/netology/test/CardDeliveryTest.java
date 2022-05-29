package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.conditions.ExactText;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.netology.datagenerator.DataGenerator;
import com.codeborne.selenide.Condition;
import org.openqa.selenium.Keys;
import ru.netology.util.ScreenShooterReportPortalExtension;


import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.util.LoggingUtils.logInfo;


@ExtendWith({ScreenShooterReportPortalExtension.class})
class CardDeliveryTest {
    DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser("Ru");

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }


    @Test
    void shouldSuccessfullySendARequestToTheCard() {
        Configuration.holdBrowserOpen = true;

        //Заполнение и первоначальная отправка формы:
        $("[data-test-id='city'] input").setValue(userInfo.getCity());
        logInfo("В поле ввода введён город " + userInfo.getCity());
        $("[data-test-id='name'] input").setValue(userInfo.getName());
        logInfo("В поле ввода введено имя " + userInfo.getName());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String scheduledDate = DataGenerator.generateDate(4);
        $("[data-test-id='date'] input").setValue(scheduledDate);
        logInfo("В поле ввода введена дата " + scheduledDate);
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        logInfo("В поле ввода введен номер телефона " + userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        logInfo("Выполнен клик по чек боксу ");
        $(byText("Запланировать")).click();
        logInfo("Выполнен клик по кнопке Запланировать ");
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + scheduledDate), Duration.ofSeconds(15));

        //Изменение ранее введенной даты и отправка формы:
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String shiftDate = DataGenerator.generateDate(12);
        $("[data-test-id='date'] input").setValue(shiftDate);
        logInfo("В поле ввода введена дата " + shiftDate);
        $(byText("Запланировать")).click();
        logInfo("Выполнен клик по кнопке Запланировать ");

        //Взаимодействие с опцией перепланировки,
        //а содержание текста и время загрузки:
        $("[data-test-id= replan-notification]")
                .shouldHave(Condition.text("Необходимо подтверждение " +
                        "У вас уже запланирована встреча на другую дату. Перепланировать?"), Duration.ofSeconds(15));
        $("[data-test-id=replan-notification] .button")
                .shouldHave(Condition.text("Перепланировать")).click();
        logInfo("Выполнен клик по кнопке Перепланировать ");
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + shiftDate), Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("Should get error message if entered wrong phone number")
    void shouldGetErrorIfWrongPhone() {
        Configuration.holdBrowserOpen = true;

        $("[data-test-id='city'] input").setValue(userInfo.getCity());
        logInfo("В поле ввода введён город " + userInfo.getCity());
        $("[data-test-id='name'] input").setValue(userInfo.getName());
        logInfo("В поле ввода введено имя " + userInfo.getName());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String scheduledDate = DataGenerator.generateDate(4);
        $("[data-test-id='date'] input").setValue(scheduledDate);
        logInfo("В поле ввода введена дата " + scheduledDate);
        $("[data-test-id='phone'] input").setValue(DataGenerator.generatePhone("en"));
        logInfo("В поле ввода введен номер телефона " + DataGenerator.generatePhone("en"));
        $("[data-test-id='agreement']").click();
        logInfo("Выполнен клик по чек боксу ");
        $(byText("Запланировать")).click();
        logInfo("Выполнен клик по кнопке Запланировать ");
        $("[data-test-id='phone'] input_sub")
                .shouldHave(new ExactText("Неверный формат номера мобильного телефона"));
    }
}
