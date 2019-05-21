package cloud.com.ydg.cloud.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yedeguo
 */
@SpringBootApplication
@ComponentScan({"com.ydg.cloud"})
public class TestDistributedLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestDistributedLockApplication.class, args);
    }

}
