package customer.cap01.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;

import cds.gen.example01service.Books;
import cds.gen.example01service.BooksDownloadContext;
import cds.gen.example01service.CsvFileEntity;
import cds.gen.example01service.DownloadContext;
import cds.gen.example01service.Example01Service_;
import cds.gen.example01service.Items;


@Component
@ServiceName(Example01Service_.CDS_NAME)
public class Example01Handler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(Example01Handler.class);

    @Autowired
    private UserInfo userInfo;

    @After(event = CqnService.EVENT_READ)
    public List<Items> afterReadItems(List<Items> items) {
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

        Items item = Items.create();
        item.setId("485fcda8-0c68-96ec-8e2a-eb8e38492bf5");
        item.setName("aaa");
        items.add(item);
        logger.info(items.size() + " records");
        return items;
    }

    // /odata/v4/Example01Service/Books/Example01Service.download() に対するハンドラ。
    // org.apache.olingo.server.core.ODataHandlerException: not implemented が発生する。
    @On(event = BooksDownloadContext.CDS_NAME)
    public void download(BooksDownloadContext context) throws IOException {
        Path path = Files.createTempFile(null, null);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.append("1,'a'");
            writer.newLine();
            writer.append("2,'b'");
            writer.newLine();
        }
        context.setResult(Files.newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE));
        context.setCompleted();
    }

    // /odata/v4/Example01Service/download() に対するハンドラ。
    // org.apache.olingo.server.core.ODataHandlerException: not implemented が発生する。
    @On(event = DownloadContext.CDS_NAME)
    public void download(DownloadContext context) throws IOException {
        Path path = Files.createTempFile(null, null);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.append("1,'a'");
            writer.newLine();
            writer.append("2,'b'");
            writer.newLine();
        }
        context.setResult(Files.newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE));
        context.setCompleted();
    }

    // /odata/v4/Example01Service/CsvFileEntity(n)/data に対するハンドラ。
    @On(event = CqnService.EVENT_READ)
    public List<CsvFileEntity> download(List<CsvFileEntity> csvFileEntities) throws IOException {
        Path path = Files.createTempFile(null, null);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.append("1,'a'");
            writer.newLine();
            writer.append("2,'b'");
            writer.newLine();
        }
        CsvFileEntity csvFileEntity = CsvFileEntity.create();
        csvFileEntity.setId(1);
        csvFileEntity.setFilename(URLEncoder.encode("あいう.csv", StandardCharsets.UTF_8));
        csvFileEntity.setData(Files.newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE));
        csvFileEntities = new ArrayList<>();
        csvFileEntities.add(csvFileEntity);
        return csvFileEntities;
    }
}
