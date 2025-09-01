package customer.cap01.handler;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;

import cds.gen.example01service.Example01Service_;
import cds.gen.example01service.Items;

@Component
@ServiceName(Example01Service_.CDS_NAME)
public class Example01Handler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(Example01Handler.class);

    @Autowired
    private UserInfo userInfo;

    @After(event = CqnService.EVENT_READ)
    public void afterReadItems(Stream<Items> items) {
        logger.info("######");
        logger.info("id=" + userInfo.getId());
        logger.info("name=" + userInfo.getName());
        logger.info("roles=" + userInfo.getRoles());
        userInfo.getAttributes().forEach((key, values) -> {
            logger.info("attr: " + key + "=" + String.join(",", values));
        });
        userInfo.getAdditionalAttributes().forEach((key, val) -> {
            logger.info("addAttr: " + key + "=" + val);
        });

        logger.info(items.toList().size() + " records");
    }
}
