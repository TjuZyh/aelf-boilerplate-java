package contractImpl;

import state.Person;
import state.PersonList;

/**
 * @Author zyh
 * @Date 2022/5/31 7:19 下午
 * @Version 1.0
 */

public interface UserManagement {
    boolean insertUser(String personList) throws Exception;

    String getAllUser() throws Exception;

    boolean updateUser(String person) throws Exception;
}
