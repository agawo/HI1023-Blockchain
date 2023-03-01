import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Block<T> {
    private T[] transactions;
    private String thisHash;
    private String previousHash;
    private long timeStamp;

    /**
     * Building block of the blockchain
     * @param transactions holds the data about the transactions;
     * @param previousHash hash of the previous block in the blockchain;
     * @param timeStamp timestamp for when the block was added to the blockchain;
     */
    public Block(T[] transactions, String previousHash, long timeStamp) {
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.thisHash = createHash();
    }

    /**
     * Creates an encrypted hash for this block, based on transaction data, previous block's hash and timestamp
     * @return hash
     */
    // this should be private, available for other classes for demonstration purposes only
    String createHash() {

        String data = Arrays.hashCode(new int[]{Arrays.hashCode(this.transactions)}) + this.previousHash
                + Long.hashCode(this.timeStamp);

        MessageDigest digest;
        byte[] bytes = new byte[0];
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(data.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
        }

        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    /**
     * Compares hash of this block with another hash
     * @param otherHash to compare
     * @return boolean
     */
    public boolean compareHash(String otherHash){
        String myHash = createHash();
        return myHash.equals(otherHash);
    }

    /**
     * Checks if the hash of this block is still valid by calculating a new hash and comparing it with the old one
     * @return boolean
     */
    public boolean isHashValid() {
        String currentHash = createHash();
        return currentHash.equals(this.thisHash);
    }


    /**
     * Returns the copy of transaction data of the block
     * @return transactions
     */
    public T[] getTransactions() {
        return Arrays.copyOf(this.transactions, this.transactions.length);
    }

    /**
     * Returns the hash of the previous block stored in this block
     * @return hash of the previous block
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Returns the hash of this block
     * @return hash of this block
     */
    public String getHash() {
        return thisHash;
    }

    /**
     * Returns the timestamp of this block
     * @return timestamp
     */
    public long getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Makes copy of this block
     * @return a copy of this block
     */
    Block<T> makeCopy() {
        return new Block<T>(this.getTransactions(), this.previousHash, this.timeStamp);
    }

    // this shouldn't exist, it's for demonstration purposes only
    // changes the hash of this block
    public void setThisHash(String thisHash) {
        this.thisHash = thisHash;
    }

    // this shouldn't exist, it's for demonstration purposes only
    // changes the transaction data of this block
    public void setTransactions(T[] transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Block{" +
                "transactions=" + Arrays.toString(transactions) +
                ", thisHash=" + thisHash +
                ", previousHash=" + previousHash +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block<?> block = (Block<?>) o;

        return timeStamp == block.timeStamp && Arrays.equals(transactions, block.transactions) && thisHash.equals(block.thisHash) && previousHash.equals(block.previousHash);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(thisHash, previousHash, timeStamp);
        result = 31 * result + Arrays.hashCode(transactions);
        return result;
    }
}
