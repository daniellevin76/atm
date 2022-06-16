import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ATMServiceTest {

    //class to be tested

    ATMService atmService;

    //Dependencies

    BankService bankService;

    CashService cashService;

    UserInput userInput;

    public ATMServiceTest(ATMService atmService, BankService bankService, CashService cashService, UserInput userInput) {
        this.atmService = atmService;
        this.bankService = bankService;
        this.cashService = cashService;
        this.userInput = userInput;
    }

    @BeforeEach
    public void setUp(){

        //Mocked objects
        userInput = mock(UserInput.class);
        bankService = mock(BankService.class);




        cashService = new CashService(bankService, atmService, userInput);


        atmService = new ATMService(bankService, userInput,cashService);





    }

    @Test
    void should_retrieveUserId_when_retrieveUser(){
        String cardId = "123456789";

        User user = new User(1L);

        when(bankService.retrieveUserByCardId(cardId)).thenReturn(
                user
        );

      assertEquals(user, atmService.retrieveUser(cardId));
    }


    @Test
    void should_return_True_when_ValidateUserWithRightCredentials() throws IOException {

        boolean loggedIn = true;
        String cardId = "123456789";
        String pin = "1234";

        when(userInput.getPinFromUser()).thenReturn(pin);
        when(bankService.authenticateUser(cardId, pin)).thenReturn(
                loggedIn
        );

        assertTrue(atmService.isLoggedIn(cardId, pin));
    }

    @Test
    void should_return_True_when_checkCardStatusIsFalse() throws IOException {

        boolean loggedIn = true;
        String cardId = "123456789";
        String pin = "1234";
        when(bankService.checkIfCardLocked(cardId)).thenReturn(
                true
        );

        atmService.insertCard(cardId);
        assertEquals(true, atmService.isCardLocked());
    }



    @Test
    void should_return_LoggedInTrue_when_LogInIsSuccessful() throws IOException {

        String cardId = "123456789";
        String pin = "1235";

        ByteArrayInputStream in = new ByteArrayInputStream(pin.getBytes());
        System.setIn(in);
        when(userInput.getPinFromUser()).thenReturn("12345");

        when(bankService.authenticateUser(cardId, pin)).thenReturn(
            true
        );
        userInput.getChoice();
        atmService.logIn(cardId);
        assertEquals(true, atmService.isLoggedIn());

    }


    @Test
    void should_return_LoggedInFalse_when_LogInIsNotSuccessful() throws IOException {
     // InputStream sysInBackUp = System.in;
        String cardId = "123456789";
        when(userInput.getPinFromUser()).thenReturn("1245");
        when(bankService.authenticateUser(cardId, userInput.getPinFromUser())).thenReturn(
                false
        );
        when(bankService.getNumberOfLoginTries()).thenReturn(
                4
        );

        atmService.logIn(cardId);


        assertEquals(false, atmService.isLoggedIn());
    //  System.setIn(sysInBackUp);
    }







    @Test
    void should_return_lockedOut_when_LogInFailsThreeTimes() throws IOException {

        String cardId = "123456789";
        when(userInput.getPinFromUser()).thenReturn("2345");

        when(bankService.authenticateUser(cardId, userInput.getPinFromUser())).thenReturn(
                false
        ).thenReturn(false).thenReturn(false);
        when(bankService.getNumberOfLoginTries()).thenReturn(
                0
        ).thenReturn(1).thenReturn(2).thenReturn(3);

        atmService.logIn(cardId);


        assertEquals(true, atmService.isCardLocked());
        //  System.setIn(sysInBackUp);
    }


    @Test
    void should_displayNumberOfAttempts_when_ThreeLoginAttemptsFails(){


        when(bankService.getNumberOfLoginTries()).
                thenReturn(1).
                thenReturn(2).
                thenReturn(3);
        assertEquals(atmService.displayMessage(bankService.getNumberOfLoginTries()),  "Number of tries: 1");
        assertEquals(atmService.displayMessage(bankService.getNumberOfLoginTries()),  "Number of tries: 2");
        assertEquals(atmService.displayMessage(bankService.getNumberOfLoginTries()),  "You're locked out");
    }
    @Test
    void should_notBeAbleToDoMoreThanThreeLoginAttemptsFails(){


        when(bankService.getNumberOfLoginTries()).
                thenReturn(1).
                thenReturn(2).
                thenReturn(3).
                thenReturn(4);
        assertEquals(atmService.displayMessage(bankService.getNumberOfLoginTries()),  "Number of tries: 1");
        assertEquals(atmService.displayMessage(bankService.getNumberOfLoginTries()),  "Number of tries: 2");
        assertNotEquals(atmService.displayMessage(bankService.getNumberOfLoginTries()),  "Number of tries: 3");


    }

    @Test
    void should_sendNumberOfLoginTriesToBankOnceWhenOneLoginAttemptMade() throws IOException {
        String cardId = "123456789";
        when(userInput.getPinFromUser()).thenReturn("1345");
        when(bankService.authenticateUser(cardId, userInput.getPinFromUser())).thenReturn(true);
        when(bankService.getNumberOfLoginTries()).thenReturn(0).thenReturn(1);
     //   when(bankService.checkBalance()).thenReturn("5000");

        atmService.logIn(cardId);

       verify(bankService, times(1)).sendNumberOfLoginTries(1);
    }

    @Test
    void should_sendNumberOfLoginTriesToBankThriceWhenThreeLoginAttemptMade() throws IOException {
        String cardId = "123456789";
        when(userInput.getPinFromUser()).thenReturn("1275");
        when(bankService.authenticateUser(cardId, userInput.getPinFromUser())).thenReturn(false).thenReturn(false).thenReturn(true);
        when(bankService.getNumberOfLoginTries()).thenReturn(0).thenReturn(1).thenReturn(2).thenReturn(3);
        when(bankService.checkBalance()).thenReturn("5000");

        atmService.logIn(cardId);

        verify(bankService, times(1)).sendNumberOfLoginTries(3);
    }

@Test
    public void should_Display5000_when_balanceChecked() {
        String amount = "5000";
    when(userInput.getAmount()).thenReturn(amount);

        when(bankService.checkBalance()).thenReturn(amount);

     assertEquals(amount, cashService.chooseServiceType(CashService.ServiceType.BALANCE_CHECK));
}


    @Test
    public void should_Display10000_when_balanceChecked() {
        String amount = "1000";
        when(userInput.getAmount()).thenReturn(amount);

        when(bankService.checkBalance()).thenReturn(amount);

        assertEquals(amount, cashService.chooseServiceType(CashService.ServiceType.BALANCE_CHECK));
    }

    @Test
    public void should_deposit5000_when_depositMethodIsCalled(){

        String amount = "5000";
        when(userInput.getAmount()).thenReturn(amount);

        assertEquals(amount, cashService.chooseServiceType(CashService.ServiceType.CASH_DEPOSIT));
    }

    @Test
    public void should_deposit500_when_depositMethodIsCalled(){

        String amount = "500";
        when(userInput.getAmount()).thenReturn(amount);

        assertEquals(amount, cashService.chooseServiceType(CashService.ServiceType.CASH_DEPOSIT));
    }

    @Test
    public void should_verifyBankServiceCashDepositRuns_when_depositMethodIsCalled(){

        String amount = "500";
        when(userInput.getAmount()).thenReturn(amount);
        cashService.chooseServiceType(CashService.ServiceType.CASH_DEPOSIT);

       verify(bankService, times(1)).deposit(amount);
    }

    @Test
    public void should_withDraw200_when_withdrawCashIsCalled(){

        String amount = "200";
        when(userInput.getAmount()).thenReturn(amount);
        cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL);

        assertEquals(amount, cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL));
    }

    @Test
    public void should_withDraw1000_when_withdrawCashIsCalled(){

        String amount = "1000";
        when(userInput.getAmount()).thenReturn(amount);
        cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL);

        assertEquals(amount, cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL));
    }

    @Test
    public void should_verifyBankServiceWithdraw_when_withdrawCashIsCalled(){

        String amount = "1000";
        when(userInput.getAmount()).thenReturn(amount);
        cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL);

        verify(bankService, times(1)).withdraw(amount);

    }

    @Test
    public void should_checkBalance_when_withdrawCashIsCalled(){

        String amount = "1000";
        when(userInput.getAmount()).thenReturn(amount);
        cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL);

        verify(bankService, times(1)).checkBalance();

    }

    @Test
    public void should_notBeAbleToWithdraw_when_checkBalanceIsLowerThanAmount(){

        String amount = "1000";
        when(userInput.getAmount()).thenReturn(amount);
        when(bankService.checkBalance()).thenReturn("500");
        cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL);

        verify(bankService, never()).withdraw(amount);

    }

    @Test
    public void should_BeAbleToWithdraw_when_checkBalanceIsHigherThanAmount(){

        String amount = "1000";
        when(userInput.getAmount()).thenReturn(amount);
        when(bankService.checkBalance()).thenReturn("2000");
        cashService.chooseServiceType(CashService.ServiceType.CASH_WITHDRAWAL);

        verify(bankService, times(1)).withdraw(amount);

    }

    @Test
    public void should_disconnectFromBank_when_ExitOptionIsChosen(){

        cashService.chooseServiceType(CashService.ServiceType.EXIT_SERVICE);

        verify(bankService, times(1)).disConnect();

    }

    @Test
    void test_static_functions() throws IOException {
String cardId = "123456789";
    try (MockedStatic<BankService> mockedBankService = mockStatic(BankService.class)) {

        mockedBankService.when(() -> BankService.retrieveBankName(cardId)).thenReturn("SEB");
        atmService.insertCard(cardId);
        verify(bankService, times(1)).retrieveBankName(cardId);
    }

}
}
