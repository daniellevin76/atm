import java.io.IOException;

public class ATMService {

    private BankService bankService;

    private UserInput userInput;

    private CashService cashService;
    private boolean loggedIn, cardLocked;

    public ATMService(BankService bankService, UserInput userInput, CashService cashService) {
        this.bankService = bankService;
        this.userInput = userInput;
        this.cashService = cashService;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isCardLocked() {
        return cardLocked;
    }



    private String pin;


    public void insertCard(String cardId) throws IOException {
        cardLocked = bankService.checkIfCardLocked(cardId);
        if(cardLocked){
            displayCardStatus();
        }else{
            displayBankName(cardId);
            logIn(cardId);
        }
    }

    private String displayBankName(String cardId) {
        return BankService.retrieveBankName(cardId);
    }

    public void logIn(String cardId) throws IOException {
    int nrOfTries;
        do {
            nrOfTries = bankService.getNumberOfLoginTries();
            pin = userInput.getPinFromUser();
            loggedIn = isLoggedIn(cardId, pin);
            nrOfTries += 1;
            bankService.sendNumberOfLoginTries(nrOfTries);
            System.out.println("nrOfTries " + nrOfTries);
        } while (!loggedIn && nrOfTries < 3);

        if (nrOfTries == 3) {
            displayMessage(nrOfTries);
            cardLocked = true;
            bankService.lockCard(cardId);
        }
        handleCash(loggedIn);
    }

    public void handleCash(boolean loggedIn){
        if (loggedIn) {
            displayServiceOptions();
            String choice = userInput.getChoice();
            cashService.chooseServiceType(CashService.ServiceType.valueOf(choice));
        }
    }
    private String displayServiceOptions() {
        return "CASH withdrawal \n CASH deposit \n BALANCE check";
    }

    public BankService getBankService() {
        return bankService;
    }
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }
    public User retrieveUser(String cardId) {
        return bankService.retrieveUserByCardId(cardId);

    }
    //Authenticate user by contacting the bank
    public boolean isLoggedIn(String cardId, String pin) {
        return bankService.authenticateUser(cardId, pin);

    }


    public String displayMessage(int tries) {
        String message = "";
        if(tries<3){
             message = "Number of tries: " + tries;
        } else{
        message = "You're locked out";
    }
    return message;
}

    public String displayCardStatus() {
        return "Sorry, your card is blocked";
    }



    public String exit() {
        bankService.disConnect();
        loggedIn = false;
        ejectCard();
      return "Bye";
    }

    private void ejectCard() {
    }
}
