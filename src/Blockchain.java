import java.util.*;

public class Blockchain<T> {
    private List<Block<T>> chain = new ArrayList<Block<T>>();

    /**
     * Blockchain is created with one empty block 0 as the root
     */
    public Blockchain() {
        @SuppressWarnings("unchecked")
        T[] empty = (T[]) new Object[0];
        this.chain.add(new Block<>(empty, "root", new Date().getTime()));
    }

    /**
     * Adds one block to the blockchain and checks if all the blocks in the blockchain are valid
     * @param transactions transaction data of the new block
     * @return true it the new block was successfully created and added
     */
    public synchronized boolean addBlock(T[] transactions){
        printState();
        return this.chain.add(new Block<>(transactions, this.chain.get(this.chain.size() - 1).getHash(), new Date().getTime()));
    }

    /**
     * Checks the validity of all the block of the blockchain
     * - if the block's hash is still valid and if the previous block's hash hasn't been changed
     * @return the list of invalid blocks
     */
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

    /**
     * Checks if all the blocks in the blockchain are valid
     * @return boolean
     */
    public boolean isValid(){
        if (getInvalidBlocks().size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Return the blockchain's length
     * @return number of blocks with the block 0
     */
    public int getChainSize(){
        return this.chain.size();
    }

    /**
     * Returns all the transactions in the blockchain, from every block
     * @return a list of all transactions
     */
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

    /**
     * Prints out all the transactions in the blockchain and marks those from invalid blocks
     */
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

    /**
     * Checks and prints out the state of the blockchain, if it's valid and if not which blocks are invalid
     */
    public synchronized void printState(){
        if(isValid()){
            System.out.println("The blockchain is valid");
        }
        else {
            System.out.println("WARNING! The blockchain is NOT valid!!!");
            System.out.println("Invalid blocks: " + getInvalidBlocks());
        }
    }

    /**
     * Compares the blockchain with other copies of it and changes the invalid blocks to the most common version in other copies
     * @param otherNodesCopies other copies of the blockchain from different nodes
     */
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

    /**
     * Returns a copy of a block from the blockchain
     * @param index of the block
     * @return copy of the block
     */
    public Block<T> getBlock(int index){
        if (index < this.chain.size()){
            return this.chain.get(index).makeCopy();
        }
        return null;
    }

    // this shouldn't exist, it's for demonstration purposes only
    // returns the true block, not just a copy of it
    public Block<T> getTrueBlock(int index){
        if (index < this.chain.size()){
            return this.chain.get(index);
        }
        return null;
    }
}
