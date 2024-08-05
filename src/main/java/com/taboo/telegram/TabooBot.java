package com.taboo.telegram;

import com.taboo.telegram.message.MessageBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
@Slf4j
@Component
public class TabooBot extends TelegramLongPollingBot {
@Getter

    private  String username;
    private final MessageBuilder messageBuilder;
    private static final String pictureUrl = "https://www.pexels.com/photo/adult-brown-tabby-cat-747795/";
    private static final String caption = "Cat";
    private static final String STICKER_FILE_ID = "CAACAgIAAxkBAAOnZrDlm9I0BIWeDsWO71-1GQhKKiEAAg4AA8A2TxMjVxREh27WZTUE";

    private  String botToken = "7401686314:AAHegXXY6c2EBFn6nDzqsiFeNoN8WX_bYqo";

    public TabooBot(@Value("${app.telegram.username}") String username,
                    @Value("${app.telegram.token}") String botToken,
                    MessageBuilder messageBuilder){
        super(botToken);
        this.username = username;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if(message.hasText()){
        String commands = "\bAvailabe commands: \n\ttext\n\tphoto\n\tcat\n\tfifi\n\tbotname\n\ttomek\n\tsticker";
        switch (update.getMessage().getText().trim().toLowerCase()){
            case "commands" -> execute(messageBuilder.buildTextMessage(chatId,commands));
            case "text" -> execute(messageBuilder.buildTextMessage(chatId,"Just a text message" ));
            case "photo","cat" -> {
                var sentPhoto =(messageBuilder.buildPhotoMessage(chatId, pictureUrl,caption));
                execute(sentPhoto);}
            case "fifi" -> {
                var sendDocument = messageBuilder.buildDocumentMessage(chatId, "This is the file ", readFile("/files/fifi.png") );
                execute(sendDocument);}
            case "botname" -> execute(messageBuilder.buildTextMessage(chatId,"Hi I'm "+getBotUsername()));
            case "tomek" ->{
                var sendTomekPicture = messageBuilder.buildDocumentMessage(chatId, "Hi myname is Tomek ",readFile("/files/tomek.jpg"));
                execute(sendTomekPicture);
            }
            case "sticker" ->{
                var sentSticker = messageBuilder.buildStickerMessage(chatId,STICKER_FILE_ID);
            execute(sentSticker);
            }
        }
        } else if (message.hasSticker()) {
            log.info("Sticker file id={}",message.getSticker().getFileId());
        }
    }

    @SneakyThrows
    private File readFile(String path){
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getFile();
    }
    @Override
    public String getBotUsername() {
        return username;
    }
}

