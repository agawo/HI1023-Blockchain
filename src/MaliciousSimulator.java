import java.util.Date;
import java.util.Random;

public class MaliciousSimulator extends Thread{
    Blockchain<String> blockchain;
    int repetitionNumber, id, type;
    Random random;

    public MaliciousSimulator(Blockchain<String> blockchain, int repetitionNumber, int type, int id) {
        this.blockchain = blockchain;
        this.repetitionNumber = repetitionNumber;
        this.type = type;
        this.id = id;
        this.random = new Random(new Date().getTime() + 2000%(id*5L + repetitionNumber));
    }
    public void run() {
        for (int i = 0; i < repetitionNumber; i++){
            System.out.println("Malicious user " + id + " is sleeping");
            try {
                sleep( (long)random.nextInt(70)*1000 + 500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Malicious user " + id + " is active");
            int chainSize = blockchain.getChainSize();
            if(chainSize > 2){
                int blockIndex = random.nextInt(chainSize - 2) + 1;
                Block<String> block = blockchain.getTrueBlock(blockIndex);
                String[] newData = block.getTransactions();
                newData[0] = overrideTransaction(newData[0]);
                block.setTransactions(newData);

                if(this.type == 1){
                    String newHash = block.createHash();
                    block.setThisHash(newHash);
                }
                System.out.println("### Malicious user " + id + " has changed block nr " + blockIndex);
            }
        }
    }

    private String overrideTransaction(String oldTransaction){
        String[] overridden = oldTransaction.split(" ");
        overridden[overridden.length - 2] += "000";
        overridden[overridden.length - 3] = "gets";
        return String.join(" ", overridden);
    }

}
