package customer.cap01.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sap.cds.services.request.UserInfo;

@RestController
public class Example01Controller {

    private static final Logger logger = LoggerFactory.getLogger(Example01Controller.class);

    @Autowired
    private UserInfo userInfo;

    @GetMapping("/rest/example01/hello")
    public String hello() {
        logger.info("@@@@@@@");
        logger.info("id=" + userInfo.getId());
        logger.info("name=" + userInfo.getName());
        logger.info("roles=" + userInfo.getRoles());
        userInfo.getAttributes().forEach((key, values) -> {
            logger.info("attr: " + key + "=" + String.join(",", values));
        });
        userInfo.getAdditionalAttributes().forEach((key, val) -> {
            logger.info("addAttr: " + key + "=" + val);
        });
        return "hello";
    }
}