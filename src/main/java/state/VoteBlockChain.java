package state;



import java.io.Serializable;

public class VoteBlockChain implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transactionId;

    private String blockHash;

    private String chainStatus;

    private Long blockHeight;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getChainStatus() {
        return chainStatus;
    }

    public void setChainStatus(String chainStatus) {
        this.chainStatus = chainStatus;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }
}
