import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class UserSimulator extends Thread{
    Blockchain<String> blockchain;
    ArrayList<Block<String>> blockchainCopy;
    String[] names, actions;
    int repetitionNumber, id;
    Random random;

    public UserSimulator(Blockchain<String> blockchain, String[] names, String[] actions, int repetitionNumber, int id) {
        this.blockchain = blockchain;
        this.names = names;
        this.actions = actions;
        this.repetitionNumber = repetitionNumber;
        this.id = id;
        this.random = new Random(new Date().getTime() + 2000%(id + repetitionNumber));
        this.blockchainCopy = new ArrayList<>();
        updateBlockchainCopy();
    }
    public void run() {
        for (int i = 0; i < repetitionNumber; i++){
            System.out.println("User " + id + " is sleeping");
            try {
                sleep( (long)random.nextInt(50)*500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("User " + id + " is making a transaction nr " + (i + 1));
            blockchain.addBlock(generateTransactions());
            updateBlockchainCopy();
        }
    }

    private String[] generateTransactions(){
        int numberOfTransactions = random.nextInt(3);
        String[] transactions = new String[numberOfTransactions + 1];
        for (int i = 0; i <= numberOfTransactions; i++){
            transactions[i] = names[random.nextInt(names.length)] + " " + actions[random.nextInt(actions.length)]
            + " " + random.nextInt(100) + " gold";
        }
        return transactions;
    }

    private void updateBlockchainCopy(){
        int size = this.blockchainCopy.size();
        if(size < this.blockchain.getChainSize()) {
            for (int i = size; i < this.blockchain.getChainSize(); i++){
                this.blockchainCopy.add(this.blockchain.getBlock(i));
            }
        }
    }

}
