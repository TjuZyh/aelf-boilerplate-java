package util;

/**
 * @Author zyh
 * @Date 2022/5/31 7:26 下午
 * @Version 1.0
 */
public class RespDataVO {
    public static String respDataVO(String info){
        if(info == null){
            return null;
        }else{
            return MyHex.HexStringToString(info);
        }
    }
}
