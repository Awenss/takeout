import org.junit.jupiter.api.Test;

import java.util.UUID;

public class Mytest {

    @Test
    public void test(){
        String name ="hhh.jpg";
        String substring = name.substring(name.lastIndexOf("."));//截取后缀

        String filName = UUID.randomUUID().toString()+substring;

    }

}
