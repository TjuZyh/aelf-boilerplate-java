package service;


import state.VoteBlockChain;

/**
 * @Author zyh
 * @Date 2022/5/10 8:37 下午
 * @Version 1.0
 */
public interface BlockChainService {
    VoteBlockChain setInfo(String className, String jsonString) throws Exception;

    String getInfo(String className) throws Exception;

    VoteBlockChain InitializeMultiLContract() throws Exception;

    /*long getBalance(String curPrivateKey) throws Exception;

    void setBalance(String toPrivateKey , long amount) throws Exception;*/


}
