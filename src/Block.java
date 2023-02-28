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

    public Block(T[] transactions, String previousHash, long timeStamp) {
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.thisHash = createHash();
    }

    public boolean compareHash(String otherHash){
        String myHash = createHash();
        return myHash.equals(otherHash);
    }

    public boolean isHashValid() {
        String currentHash = createHash();
        return currentHash.equals(this.thisHash);
    }

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

    public T[] getTransactions() {
        return Arrays.copyOf(this.transactions, this.transactions.length);
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return thisHash;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    // this shouldn't exist, it's for demonstration purposes only
    public void setThisHash(String thisHash) {
        this.thisHash = thisHash;
    }

    // this shouldn't exist, it's for demonstration purposes only
    public void setTransactions(T[] transactions) {
        this.transactions = transactions;
    }

    public Block<T> makeCopy() {
        return new Block<T>(this.getTransactions(), this.previousHash, this.timeStamp);
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
        //timeStamp == block.timeStamp &&
        return Arrays.equals(transactions, block.transactions) && thisHash.equals(block.thisHash) && previousHash.equals(block.previousHash);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(thisHash, previousHash, timeStamp);
        result = 31 * result + Arrays.hashCode(transactions);
        return result;
    }
}
