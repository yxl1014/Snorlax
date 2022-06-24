import com.drew.imaging.ImageProcessingException;
import yxl.demo.ImgUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Test {
    public static final File file = new File("/home/yxl/图片/微信图片_20220621191356.jpg");
    public static void main(String[] args) throws ImageProcessingException, IOException {
        ImgUtil.readImgAll(file);

        System.out.println("===============================");
        Map map = ImgUtil.readImgNormal(file);
        System.out.println(map);
        System.out.println("===============================");
        Map add = ImgUtil.parseAdd("116.3039", "39.97646");
        System.out.println(add);
        System.out.println("===============================");
        Map add1d = ImgUtil.parseAddByImg(file);
        System.out.println(add1d);
    }
}
