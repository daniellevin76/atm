public  class BankService {

    public static String retrieveBankName(String cardId) {
        return "Bank Name";
    }



    //Retrieve users userId from bank, this is not displayed to user
    //when asked for pin
    public User retrieveUserByCardId(String cardId) {
        return null;
    }

    //Send user credentials to bank for verification
    public boolean authenticateUser(String cardId, String pin) {
        return false;
    }

    //Retrieves number of failed logins from bank
    public int getNumberOfLoginTries() {
        return 0;
    }

    public void sendNumberOfLoginTries(int numberOfLoginTries) {
    }



    public void lockCard(String cardId) {
    }

    public boolean checkIfCardLocked(String cardId) {
        return false;
    }



    public String checkBalance() {

        return null;
    }

    public void deposit(String amount) {
    }

    public void withdraw(String amount) {
    }

    public void updateBalance(String valueOf) {
    }

    public void disConnect() {
    }
}
