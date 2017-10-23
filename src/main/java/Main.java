import controller.SenderController;
import entity.Loan;

public class Main {

    public static void main(String[] args) {

        SenderController send = new SenderController("guest","datdb.cphbusiness.dk","logs");
        Loan loan = new Loan(12345678,345,45,26);
        send.sendMessage(loan);
        send.close();

    }
}
