import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Blockchain<String> transactionsLog= new Blockchain<String>();

        transactionsLog.addBlock(new String[]{"Thorkell has 80 gold", "Hyetta has 30 gold", "Clementine has 50 gold"});

        UserSimulator[] users = new UserSimulator[3];
        // changes both transaction and hash
        MaliciousSimulator maliciousUser1 = new MaliciousSimulator(transactionsLog, 1, 1,1);
        // changes only transaction
        MaliciousSimulator maliciousUser2 = new MaliciousSimulator(transactionsLog, 1, 2,2);

        for(int i = 0; i < users.length; i++){
            users[i] = new UserSimulator(transactionsLog, new String[]{"Thorkell", "Hyetta", "Clementine"},
                    new String[]{"pays", "gets"}, 4, i+1);
        }

        for(int i = 0; i < users.length; i++){
            users[i].start();
        }
        maliciousUser1.start();
        maliciousUser2.start();

        for(int i = 0; i < users.length; i++){
            try{
                users[i].join();
            }
            catch (Exception ignored){ }

        }

        try{
            maliciousUser1.join();
            maliciousUser2.join();
        }
        catch (Exception ignored){ }

        System.out.println();
        System.out.println("______________Results_______________");
        transactionsLog.printState();
        transactionsLog.printAllTransactions();
        System.out.println();
        System.out.println("______________Restore the invalid blocks_______________");
        ArrayList<ArrayList<Block<String>>> copies = new ArrayList<ArrayList<Block<String>>>();
        for(int i = 0; i < users.length; i++){
            copies.add(users[i].blockchainCopy);
        }
        transactionsLog.validateBlockchain(copies);
        transactionsLog.printState();
        transactionsLog.printAllTransactions();
    }

}