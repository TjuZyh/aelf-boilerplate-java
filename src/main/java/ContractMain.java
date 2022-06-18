import contractImpl.UserManagementImpl;
import io.aelf.utils.JsonUtil;
import service.BlockChainServiceImpl;

/**
 * @Author zyh
 * @Date 2022/5/31 7:46 下午
 * @Version 1.0
 */
//调用jar包的格式为：java -jar UserManagement.jar 函数名 参数1 参数2
//参数为字符串 必须传入 如果没有就传"null" 注意字符串为必须为json形式
public class ContractMain {
    private static BlockChainServiceImpl blockChainService = new BlockChainServiceImpl();
    private static Integer cnt = 0;

    //线程不安全，需要上锁
    public static void startService() throws Exception {
        if(cnt == 0){
            blockChainService.InitializeMultiLContract();
            cnt++;
        }
    }

    public static void main(String[] args) throws Exception {
        startService();
        String functionName = args[0];
        String params = args[1];

        UserManagementImpl userManagement = new UserManagementImpl();
        if(functionName.equals("insertUser")){
            boolean insertStatus = userManagement.insertUser(params);
            if(insertStatus){
                System.out.println("success");
            }else{
                System.out.println("fail");
            }
        }else if(functionName.equals("getAllUser")){
            String allUser = userManagement.getAllUser();
            System.out.println(allUser);
        }else if(functionName.equals("updateUser")){
            boolean updateStatus = userManagement.updateUser(params);
            if(updateStatus){
                System.out.println("success");
            }else{
                System.out.println("fail");
            }
        }else{
            System.out.println("invalid functionName");
        }
    }
}
