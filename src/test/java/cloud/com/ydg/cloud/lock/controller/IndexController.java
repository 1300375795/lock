package cloud.com.ydg.cloud.lock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YDG
 * @description
 * @since 2019-05-21
 */
@Slf4j
@RestController
public class IndexController {

    @GetMapping("index")
    public void index() {
        System.out.println("");
    }

}
