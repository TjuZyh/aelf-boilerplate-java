package contractImpl;

import com.alibaba.fastjson.JSONObject;
import service.BlockChainServiceImpl;
import state.Person;
import state.PersonList;
import state.VoteBlockChain;
import util.RespDataVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zyh
 * @Date 2022/5/31 7:22 下午
 * @Version 1.0
 */
public class UserManagementImpl implements UserManagement {

    //添加用户信息
    @Override
    public boolean insertUser(String personList) throws Exception {
        BlockChainServiceImpl blockChainService = new BlockChainServiceImpl();
        String respInfo = blockChainService.getInfo("personList");
        VoteBlockChain info = blockChainService.setInfo("personList", JSONObject.toJSONString(personList));
        if(info.getChainStatus().equals("MINED")){
            return true;
        }else{
            return false;
        }
    }

    //获取全部用户信息
    @Override
    public String getAllUser() throws Exception {
        BlockChainServiceImpl blockChainService = new BlockChainServiceImpl();
        String respInfo = blockChainService.getInfo("person");
        return RespDataVO.respDataVO(respInfo);
    }

    //更新用户信息
    @Override
    public boolean updateUser(String person) throws Exception{
        BlockChainServiceImpl blockChainService = new BlockChainServiceImpl();
        String respInfo = blockChainService.getInfo("personList");
        //将json转换为类
        PersonList respPersonList = JSONObject.parseObject(RespDataVO.respDataVO(respInfo) , PersonList.class);
        respPersonList.getPersonList().add(JSONObject.parseObject(person , Person.class));
        VoteBlockChain info = blockChainService.setInfo("personList", JSONObject.toJSONString(respPersonList));
        if(info.getChainStatus().equals("MINED")){
            return true;
        }else{
            return false;
        }
    }
}
