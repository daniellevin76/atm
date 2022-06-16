
public class CashService {



    private BankService bankService;
    private ATMService atmService;

    private UserInput userInput;

    public CashService(BankService bankService, ATMService atmService, UserInput userInput) {
        this.bankService = bankService;
        this.atmService = atmService;
        this.userInput = userInput;
    }

    enum ServiceType {
        CASH_WITHDRAWAL,
        CASH_DEPOSIT,
        BALANCE_CHECK,
        EXIT_SERVICE
    }


    public String chooseServiceType(ServiceType serviceType){

        switch(serviceType) {
            case CASH_WITHDRAWAL:
                return withdrawCash();
            case CASH_DEPOSIT:
                return depositCash();
            case BALANCE_CHECK:
              return bankService.checkBalance();
            case EXIT_SERVICE:
                return atmService.exit();
        }

        return null;
    }

    private String withdrawCash() {
        int balance = Integer.parseInt(bankService.checkBalance());
        int amount = Integer.parseInt(userInput.getAmount());
        if(balance > amount) {
            bankService.withdraw(String.valueOf(amount));
            bankService.updateBalance(String.valueOf(balance - amount));
        } else {
            displayBalance(bankService.checkBalance());
        }
        return String.valueOf(amount);
    }

    private String displayBalance(String checkBalance) {
        return checkBalance;
    }

    public String depositCash() {

        String amount = userInput.getAmount();
        bankService.deposit(amount);

        return amount;
    }

}
