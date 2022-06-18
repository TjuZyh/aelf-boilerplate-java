package service;

import com.google.protobuf.ByteString;
import io.aelf.protobuf.generated.Client;
import io.aelf.protobuf.generated.Core;
import io.aelf.protobuf.generated.MultiLanguageContractOuterClass;
import io.aelf.protobuf.generated.TokenContract;
import io.aelf.schemas.ExecuteTransactionDto;
import io.aelf.schemas.SendTransactionInput;
import io.aelf.schemas.SendTransactionOutput;
import io.aelf.schemas.TransactionResultDto;
import io.aelf.sdk.AElfClient;
import io.aelf.utils.ByteArrayHelper;
import io.aelf.utils.Sha256;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Hex;
import state.VoteBlockChain;

/**
 * @Author zyh
 * @Date 2022/5/10 8:40 下午
 * @Version 1.0
 */
public class BlockChainServiceImpl implements BlockChainService{

    private AElfClient multiLClient = new AElfClient("http://127.0.0.1:1235");
    private String privateKey = "b465f9d58248a49fd708fd1147b96e7e7be6f017e2c3beb6f529b1a6b0788b70";
    private String dataStoreContractAddress = "2LUmicHyH4RXrMjG4beDwuDsiWJESyLkgkwPdGTR8kahRzq5XS";

    @Override
    public VoteBlockChain setInfo(String className, String jsonString) throws Exception {
        // 通过节点 privateKey 获取节点地址，该地址即为合约的 owner
        String ownerAddress = multiLClient.getAddressFromPrivateKey(privateKey);
        Client.Address.Builder owner = Client.Address.newBuilder();
        owner.setValue(ByteString.copyFrom(Base58.decodeChecked(ownerAddress)));

        // 构建合约调用时需要传递的参数
        // 具体定义见 io.aelf 包中的 proto 文件
        MultiLanguageContractOuterClass.SetInput.Builder multiLanguageSetInput = MultiLanguageContractOuterClass.SetInput.newBuilder();
        // 对不同字段设置相应值
        multiLanguageSetInput.setClassName(className);
        multiLanguageSetInput.setJsonString(jsonString);
        MultiLanguageContractOuterClass.SetInput setInputObj = multiLanguageSetInput.build();
        // 构建调用智能合约方法对应的参数
        Core.Transaction.Builder transactionDataStore = multiLClient.generateTransaction(ownerAddress, dataStoreContractAddress, "Set", setInputObj.toByteArray());
        Core.Transaction transactionDataStoreObj = transactionDataStore.build();
        // 用 owner 地址对该交易签名
        String signature = multiLClient.signTransaction(privateKey, transactionDataStoreObj);
        transactionDataStore.setSignature(ByteString.copyFrom(ByteArrayHelper.hexToByteArray(signature)));
        transactionDataStoreObj = transactionDataStore.build();

        // 发送交易，该逻辑主要对应合约中的set方法
        SendTransactionInput sendTransactionInputObj = new SendTransactionInput();
        sendTransactionInputObj.setRawTransaction(Hex.toHexString(transactionDataStoreObj.toByteArray()));
        SendTransactionOutput sendResult = multiLClient.sendTransaction(sendTransactionInputObj);
        TransactionResultDto transactionResult;
        // 循环查询接口，根据id获得交易执行结果
        while (true) {
            transactionResult = multiLClient.getTransactionResult(sendResult.getTransactionId());
            System.out.println(transactionResult);
            if ("MINED".equals(transactionResult.getStatus())) {
                VoteBlockChain voteBlockChain = new VoteBlockChain();
                voteBlockChain.setTransactionId(transactionResult.getTransactionId());
                voteBlockChain.setBlockHeight(transactionResult.getBlockNumber());
                voteBlockChain.setBlockHash(transactionResult.getBlockHash());
                voteBlockChain.setChainStatus(transactionResult.getStatus());
                // 当状态为MINED表示执行成功，直接返回

                return  voteBlockChain ;
            } else if ("PENDING".equals(transactionResult.getStatus())) {
                // 当状态为PENDING表示还未获取到结果，等待
                Thread.sleep(300);
            } else {
                // 若其他结果则抛出异常
                throw new Exception();
            }
        }
    }

    @Override
    public String getInfo(String className) throws Exception {
        String ownerAddress = multiLClient.getAddressFromPrivateKey(privateKey);
        Client.Address.Builder owner = Client.Address.newBuilder();
        owner.setValue(ByteString.copyFrom(Base58.decodeChecked(ownerAddress)));

        // 构建合约调用时需要传递的参数
        // 具体定义见 io.aelf 包中的 proto 文件
        MultiLanguageContractOuterClass.GetInput.Builder multiLanguageGetInput = MultiLanguageContractOuterClass.GetInput.newBuilder();
        // 对不同字段设置相应值
        multiLanguageGetInput.setClassName(className);
        MultiLanguageContractOuterClass.GetInput getInputObj = multiLanguageGetInput.build();
        // 构建调用智能合约方法对应的参数
        Core.Transaction.Builder transactionDataStore = multiLClient.generateTransaction(ownerAddress, dataStoreContractAddress, "Get", getInputObj.toByteArray());
        Core.Transaction transactionDataStoreObj = transactionDataStore.build();
        // 用 owner 地址对该交易签名
        String signature = multiLClient.signTransaction(privateKey, transactionDataStoreObj);
        transactionDataStore.setSignature(ByteString.copyFrom(ByteArrayHelper.hexToByteArray(signature)));
        transactionDataStoreObj = transactionDataStore.build();

        // 发送交易，该逻辑主要对应合约中的set方法
        SendTransactionInput sendTransactionInputObj = new SendTransactionInput();
        sendTransactionInputObj.setRawTransaction(Hex.toHexString(transactionDataStoreObj.toByteArray()));
        SendTransactionOutput sendResult = multiLClient.sendTransaction(sendTransactionInputObj);
        System.out.println(sendResult);
        TransactionResultDto transactionResult;
        // 循环查询接口，根据id获得交易执行结果
        while (true) {
            transactionResult = multiLClient.getTransactionResult(sendResult.getTransactionId());
            System.out.println(transactionResult);

            if ("MINED".equals(transactionResult.getStatus())) {

                // 当状态为MINED表示执行成功，直接返回

                System.out.println("未解码："+transactionResult.getReturnValue());;

                return  transactionResult.getReturnValue() ;
            } else if ("PENDING".equals(transactionResult.getStatus())) {
                // 当状态为PENDING表示还未获取到结果，等待
                Thread.sleep(300);
            } else {
                // 若其他结果则抛出异常
                throw new Exception();
            }
        }
    }

    @Override
    public VoteBlockChain InitializeMultiLContract() throws Exception {
        // 通过节点 privateKey 获取节点地址，该地址即为合约的 owner
        String ownerAddress = multiLClient.getAddressFromPrivateKey(privateKey);
        Client.Address.Builder owner = Client.Address.newBuilder();
        owner.setValue(ByteString.copyFrom(Base58.decodeChecked(ownerAddress)));

        // 构建合约调用时需要传递的参数
        // 具体定义见 io.aelf 包中的 proto 文件
        MultiLanguageContractOuterClass.InitializeInput.Builder initializeInput = MultiLanguageContractOuterClass.InitializeInput.newBuilder();
        // 对不同字段设置相应值
        initializeInput.setFlag("0");
        MultiLanguageContractOuterClass.InitializeInput initializeObj = initializeInput.build();

        /// 构建调用智能合约方法对应的参数
        Core.Transaction.Builder transactionDataStore = multiLClient.generateTransaction(ownerAddress, dataStoreContractAddress, "Initialize", initializeObj.toByteArray());
        Core.Transaction transactionDataStoreObj = transactionDataStore.build();
        // 用 owner 地址对该交易签名
        String signature = multiLClient.signTransaction(privateKey, transactionDataStoreObj);
        transactionDataStore.setSignature(ByteString.copyFrom(ByteArrayHelper.hexToByteArray(signature)));
        transactionDataStoreObj = transactionDataStore.build();

        // 发送交易，该逻辑主要对应合约中的set方法
        SendTransactionInput sendTransactionInputObj = new SendTransactionInput();
        sendTransactionInputObj.setRawTransaction(Hex.toHexString(transactionDataStoreObj.toByteArray()));
        SendTransactionOutput sendResult = multiLClient.sendTransaction(sendTransactionInputObj);
        TransactionResultDto transactionResult;
        // 循环查询接口，根据id获得交易执行结果
        while (true) {
            transactionResult = multiLClient.getTransactionResult(sendResult.getTransactionId());
            System.out.println(transactionResult);
            if ("MINED".equals(transactionResult.getStatus())) {
                VoteBlockChain voteBlockChain = new VoteBlockChain();
                voteBlockChain.setTransactionId(transactionResult.getTransactionId());
                voteBlockChain.setBlockHeight(transactionResult.getBlockNumber());
                voteBlockChain.setBlockHash(transactionResult.getBlockHash());
                voteBlockChain.setChainStatus(transactionResult.getStatus());
                // 当状态为MINED表示执行成功，直接返回

                return  voteBlockChain ;
            } else if ("PENDING".equals(transactionResult.getStatus())) {
                // 当状态为PENDING表示还未获取到结果，等待
                Thread.sleep(300);
            } else {
                // 若其他结果则抛出异常
                throw new Exception();
            }
        }
    }

    /*@Override
    public long getBalance(String curPrivateKey) throws Exception {

        // 通过初始合约的名称获取合约地址
        String tokenContractAddress = multiLClient.getContractAddressByName(privateKey, Sha256.getBytesSha256("AElf.ContractNames.Token"));

        *//*Client.Address.Builder to = Client.Address.newBuilder();
        to.setValue(ByteString.copyFrom(Base58.decodeChecked("7s4XoUHfPuqoZAwnTV7pHWZAaivMiL8aZrDSnY9brE1woa8vz")));
        Client.Address toObj = to.build();*//*

        //设置转账需要参数：发送的地址，symbol，金额，memo
        *//*TokenContract.TransferInput.Builder paramTransfer = TokenContract.TransferInput.newBuilder();
        paramTransfer.setTo(toObj);
        paramTransfer.setSymbol("ELF");
        paramTransfer.setAmount(1000000000);
        paramTransfer.setMemo("transfer in demo");
        TokenContract.TransferInput paramTransferObj = paramTransfer.build();*//*

        //通过privateKey获取地址
        String ownerAddress = multiLClient.getAddressFromPrivateKey(privateKey);
        String curAddress = multiLClient.getAddressFromPrivateKey(curPrivateKey);

        *//*Core.Transaction.Builder transactionTransfer = multiLClient.generateTransaction(ownerAddress, tokenContractAddress, "Transfer", paramTransferObj.toByteArray());
        Core.Transaction transactionTransferObj = transactionTransfer.build();
        transactionTransfer.setSignature(ByteString.copyFrom(ByteArrayHelper.hexToByteArray(multiLClient.signTransaction(privateKey, transactionTransferObj))));
        transactionTransferObj = transactionTransfer.build();

        // Send the transfer transaction to AElf chain node.
        SendTransactionInput sendTransactionInputObj = new SendTransactionInput();
        sendTransactionInputObj.setRawTransaction(Hex.toHexString(transactionTransferObj.toByteArray()));
        SendTransactionOutput sendResult = multiLClient.sendTransaction(sendTransactionInputObj);

        Thread.sleep(4000);

        // After the transaction is mined, query the execution results.
        TransactionResultDto transactionResult = multiLClient.getTransactionResult(sendResult.getTransactionId());
        System.out.println(transactionResult.getStatus());*//*

        // Query account balance.
        Client.Address.Builder owner = Client.Address.newBuilder();
        //owner.setValue(ByteString.copyFrom(Base58.decodeChecked(ownerAddress)));
        owner.setValue(ByteString.copyFrom(Base58.decodeChecked(curAddress)));
        Client.Address ownerObj = owner.build();

        TokenContract.GetBalanceInput.Builder paramGetBalance = TokenContract.GetBalanceInput.newBuilder();
        paramGetBalance.setSymbol("ELF");
        paramGetBalance.setOwner(ownerObj);
        TokenContract.GetBalanceInput paramGetBalanceObj = paramGetBalance.build();

        Core.Transaction.Builder transactionGetBalance = multiLClient.generateTransaction(ownerAddress, tokenContractAddress, "GetBalance", paramGetBalanceObj.toByteArray());
        Core.Transaction transactionGetBalanceObj = transactionGetBalance.build();
        String signature = multiLClient.signTransaction(privateKey, transactionGetBalanceObj);
        transactionGetBalance.setSignature(ByteString.copyFrom(ByteArrayHelper.hexToByteArray(signature)));
        transactionGetBalanceObj = transactionGetBalance.build();

        ExecuteTransactionDto executeTransactionDto = new ExecuteTransactionDto();
        executeTransactionDto.setRawTransaction(Hex.toHexString(transactionGetBalanceObj.toByteArray()));
        String transactionGetBalanceResult = multiLClient.executeTransaction(executeTransactionDto);

        TokenContract.GetBalanceOutput balance = TokenContract.GetBalanceOutput.getDefaultInstance().parseFrom(ByteArrayHelper.hexToByteArray(transactionGetBalanceResult));
        //System.out.println(balance.getBalance());
        return balance.getBalance();
    }

    @Override
    public void setBalance(String toPrivateKey , long amount) throws Exception {
        // 通过初始合约的名称获取合约地址
        String tokenContractAddress = multiLClient.getContractAddressByName(privateKey, Sha256.getBytesSha256("AElf.ContractNames.Token"));

        String ownerAddress = multiLClient.getAddressFromPrivateKey(privateKey);
        String toAddress = multiLClient.getAddressFromPrivateKey(toPrivateKey);

        Client.Address.Builder to = Client.Address.newBuilder();
        to.setValue(ByteString.copyFrom(Base58.decodeChecked(toAddress)));
        Client.Address toObj = to.build();

        //设置转账需要参数：发送的地址，symbol，金额，memo
        TokenContract.TransferInput.Builder paramTransfer = TokenContract.TransferInput.newBuilder();
        paramTransfer.setTo(toObj);
        paramTransfer.setSymbol("ELF");
        paramTransfer.setAmount(amount);
        paramTransfer.setMemo("transfer in demo");
        TokenContract.TransferInput paramTransferObj = paramTransfer.build();

        //通过privateKey获取地址
        //String ownerAddress = multiLClient.getAddressFromPrivateKey(privateKey);
        //String curAddress = multiLClient.getAddressFromPrivateKey(curPrivateKey);

        Core.Transaction.Builder transactionTransfer = multiLClient.generateTransaction(ownerAddress, tokenContractAddress, "Transfer", paramTransferObj.toByteArray());
        Core.Transaction transactionTransferObj = transactionTransfer.build();
        transactionTransfer.setSignature(ByteString.copyFrom(ByteArrayHelper.hexToByteArray(multiLClient.signTransaction(privateKey, transactionTransferObj))));
        transactionTransferObj = transactionTransfer.build();

        // Send the transfer transaction to AElf chain node.
        SendTransactionInput sendTransactionInputObj = new SendTransactionInput();
        sendTransactionInputObj.setRawTransaction(Hex.toHexString(transactionTransferObj.toByteArray()));
        SendTransactionOutput sendResult = multiLClient.sendTransaction(sendTransactionInputObj);

        Thread.sleep(4000);

        // After the transaction is mined, query the execution results.
        TransactionResultDto transactionResult = multiLClient.getTransactionResult(sendResult.getTransactionId());
        System.out.println(transactionResult.getStatus());
    }*/
}
