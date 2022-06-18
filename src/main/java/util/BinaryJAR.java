package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author zyh
 * @Date 2022/5/31 8:02 下午
 * @Version 1.0
 */
public class BinaryJAR {
    public static byte[] readFileByBinary(File file) throws IOException {
        String curFilePath = file.getAbsolutePath();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[fileInputStream.available()];
        fileInputStream.read(fileBytes);
        fileInputStream.close();
        return fileBytes;
    }
}
