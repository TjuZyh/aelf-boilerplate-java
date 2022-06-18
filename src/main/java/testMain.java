//import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONObject;
import contractImpl.UserManagementImpl;
import service.BlockChainServiceImpl;
import state.Person;
import util.MyHex;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zyh
 * @Date 2022/5/31 10:05 下午
 * @Version 1.0
 */
public class testMain {
    public static void main(String[] args) throws Exception {
        //UserManagementImpl userManagement = new UserManagementImpl();
        BlockChainServiceImpl blockChainService = new BlockChainServiceImpl();

        //String allUser = userManagement.getAllUser();
        /*List<Person> personList = new ArrayList<>();
        Person person1 = new Person();
        person1.setId(2);
        person1.setName("fy");
        personList.add(person1);
        String jsonString = JSONObject.toJSONString(personList);
        System.out.println(userManagement.insertUser(jsonString));*/

        String info = blockChainService.getInfo("aaa");
        System.out.println(MyHex.HexStringToString(info));
        //blockChainService.setInfo("personList"  , jsonString);

        /*Person person = new Person();
        person.setName("aaa");
        person.setId(111);

        blockChainService.setInfo("aaa" , JSONObject.toJSONString(person));*/

    }
}
