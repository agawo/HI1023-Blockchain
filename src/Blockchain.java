import java.util.*;

public class Blockchain<T> {
    private List<Block<T>> chain = new ArrayList<Block<T>>();

    public Blockchain() {
        @SuppressWarnings("unchecked")
        T[] empty = (T[]) new Object[0];
        this.chain.add(new Block<>(empty, "root", new Date().getTime()));
    }

    public synchronized boolean addBlock(T[] transactions){
        printState();
        this.chain.add(new Block<>(transactions, this.chain.get(this.chain.size() - 1).getHash(), new Date().getTime()));
        return true;
    }

    public boolean isValidOld() {
        boolean result = true;
        for (int i = 0; i < this.chain.size() - 1; i++) {
            if(!this.chain.get(i).isHashValid()){
                System.out.println("WARNING!!!");
                System.out.println("Invalid hash on the block nr " + i);
                System.out.println("The invalid transaction: " + Arrays.toString(this.chain.get(i).getTransactions()));
                result = false;
                continue;
            }
            if (!this.chain.get(i).compareHash(this.chain.get(i + 1).getPreviousHash())) {
                System.out.println("WARNING!!!");
                System.out.println("Invalid block nr " + i);
                System.out.println("The invalid transaction: " + Arrays.toString(this.chain.get(i).getTransactions()));
                result = false;
            }
        }
        return result;
    }

    private ArrayList<Integer> getInvalidBlocks(){
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < this.chain.size() - 1; i++) {
            if(!this.chain.get(i).isHashValid()){
                result.add(i);
                continue;
            }
            if (!this.chain.get(i).compareHash(this.chain.get(i + 1).getPreviousHash())) {
                result.add(i);
            }
        }
        return result;
    }

    public boolean isValid(){
        if (getInvalidBlocks().size() == 0) {
            return true;
        }
        return false;
    }

    public int getChainSize(){
        return this.chain.size();
    }

    public synchronized ArrayList<T[]> getTransactions() {
        ArrayList<T[]> transactions = new ArrayList<>();
        if(this.chain.size() == 1){
            return transactions;
        }
        for (int i = 1; i < this.chain.size(); i++) {
            transactions.add(this.chain.get(i).getTransactions());
        }
        return transactions;
    }

    public synchronized void printState(){
        if(isValid()){
            System.out.println("The blockchain is valid");
        }
        else {
            System.out.println("The blockchain is NOT valid!!!");
            System.out.println("Invalid blocks: " + getInvalidBlocks());
        }
    }

    public synchronized void printAllTransactions(){
        ArrayList<Integer> invalidBlocks = this.getInvalidBlocks();
        int index = 1;
        for (T[] transaction:this.getTransactions()) {
            System.out.print(Arrays.toString(transaction));
            if(invalidBlocks.contains(index)) {
                System.out.print("     <--- WARNING! This transaction is invalid!!!");
            }
            System.out.println();
            index++;
        }
    }

    public synchronized void validateBlockchain(ArrayList<ArrayList<Block<T>>> otherNodesCopies) {
        ArrayList<Integer> invalidBlocks = getInvalidBlocks();
        for (Integer index: invalidBlocks) {
            Map<Block<T>, Integer> versions = new HashMap<Block<T>, Integer>();
            for (ArrayList<Block<T>> list:otherNodesCopies) {
                if(list.size() > index){
                    Block<T> block = list.get(index);
                    if(versions.containsKey(block)){
                        versions.put(block, versions.get(block)+1);
                    } else {
                        versions.put(block, 1);
                    }
                }
            }
            Block<T> mostCommonBlock = Collections.max(versions.entrySet(), Map.Entry.comparingByValue()).getKey();
            this.chain.set(index, mostCommonBlock);
        }
    }

    @Override
    public String toString() {
        return "Blockchain{" +
                "chain=" + chain +
                '}';
    }

    public Block<T> getBlock(int index){
        if (index < this.chain.size()){
            return this.chain.get(index).makeCopy();
        }
        return null;
    }

    // this shouldn't exist, it's for demonstration purposes only
    public Block<T> getTrueBlock(int index){
        if (index < this.chain.size()){
            return this.chain.get(index);
        }
        return null;
    }
}
