package ru.netology.web.steps;


import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.Тогда;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.TransferPage;
import ru.netology.web.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

public class MySteps {
    private final AkitaScenario scenario = AkitaScenario.getInstance();

    @Пусть("^пользователь залогинен с именем \"([^\"]*)\" и паролем \"([^\"]*)\";$")
    public void loginWithNameAndPassword(String login, String password) {
        // из .properties файла читаем свойство loginUrl
        var loginUrl = loadProperty("loginUrl");
        open(loginUrl);
        scenario.setCurrentPage(page(LoginPage.class));
        var loginPage = (LoginPage) scenario.getCurrentPage().appeared();
        var authInfo = new DataHelper.AuthInfo(login, password);
        scenario.setCurrentPage(loginPage.validLogin(authInfo));
        var verificationPage = (VerificationPage) scenario.getCurrentPage().appeared();
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        scenario.setCurrentPage(verificationPage.validVerify(verificationCode));
        scenario.getCurrentPage().appeared();
    }


    @Когда("^он переводит \"([^\"]*)\" рублей с карты с номером \"([^\"]*)\" на свою \"([^\"]*)\" карту со страницы перевода средств;$")
    public void transferMoneyFromSecondToFirstCard(String amount, String fromCard, String firstCard) {
        var dashboardPage = (DashboardPage) scenario.getCurrentPage().appeared();
        scenario.setCurrentPage(dashboardPage.depositToFirstCard());
        var transferPage = (TransferPage) scenario.getCurrentPage().appeared();
        scenario.setCurrentPage(transferPage.transferMoney(amount, fromCard));
        scenario.getCurrentPage().appeared();
    }


    @Тогда("^баланс его \"([^\"]*)\" карты из списка на главной странице должен стать \"([^\"]*)\" рублей\\.$")
    public void checkBalanceFirstCard(String firstCardNumber, String expectedBalance) {
        var dashboardPage = (DashboardPage) scenario.getCurrentPage().appeared();
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo().getCardNumber());
        Assertions.assertEquals(expectedBalance.replace(" ", ""), String.valueOf(firstCardBalance));
    }
}
