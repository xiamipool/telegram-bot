package com.xiamipool.bot.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpHost;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiamipool.bot.constant.Constant.Command.AIRDROP;
import static com.xiamipool.bot.constant.Constant.Command.HELP;

@Service
public class CommandService {

    private final static String API_BOT_URL_TEST = "https://api.telegram.org/bot1813773806:AAF0D996Js7QxcX385js6uD2fpHIMkrJbEo";
    private final static String API_BOT_URL = "https://api.telegram.org/bot1707053096:AAE7yQ7cqSUB9xG6Ryev09UPPINxdLQe54E";

    private static Map<String, String> maps = new HashMap<>();

    static {
        maps.put("/airdrop", "You are now participating in the XiaMiPool airdrop!\n" +
                "For every ticket you will receive more $XMPT tokens in our airdrop. Refer your friends through your unique URL to receive more tickets.");
    }

    public boolean hook(String body) throws Exception {
        Message message = new Message();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msg = jsonObject.getJSONObject("message");
        String cmd = msg.getString("text");
        message.setChat(JSONObject.parseObject(msg.getString("chat"), Chat.class));
        if (AIRDROP.equals(cmd)) {
            message.setText(getText(cmd));
            sendTextMsg(message);
        } else if (HELP.equals(cmd)) {
            sendGraphicsMsg(message);
        } else {
            sendGraphicsMsg(message);
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        Unirest.setProxy(new HttpHost("127.0.0.1", 10809));
        JSONArray menus = JSONObject.parseArray(getResource(new ClassPathResource("init/menu.json")));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        Message message = new Message();
        for (int i = 0; i < menus.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            JSONObject jsonObject = menus.getJSONObject(i);
            message.setText(jsonObject.getString("text"));
            JSONArray buttons = jsonObject.getJSONArray("children");
            for (int j = 0; j < buttons.size(); j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                JSONObject json = buttons.getJSONObject(j);
                button.setText(json.getString("text"));
                button.setCallbackData(json.getString("callback_data"));
                button.setUrl(json.getString("url"));
                row.add(button);
            }
            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        HttpResponse<JsonNode> res = Unirest.post(API_BOT_URL + "/sendMessage")
                .field("chat_id", "1892245219")
                .field("text", message.getText())
                .field("reply_markup", new ObjectMapper().writeValueAsString(inlineKeyboardMarkup))
                .asJson();
        System.out.println(res);
    }

    private void sendGraphicsMsg(Message message) throws Exception {
        JSONArray menus = JSONObject.parseArray(getResource(new ClassPathResource("init/menu.json")));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < menus.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            JSONObject jsonObject = menus.getJSONObject(i);
            message.setText(jsonObject.getString("text"));
            JSONArray buttons = jsonObject.getJSONArray("children");
            for (int j = 0; j < buttons.size(); j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                JSONObject json = buttons.getJSONObject(j);
                button.setText(json.getString("text"));
                button.setCallbackData(json.getString("callback_data"));
                button.setUrl(json.getString("url"));
                row.add(button);
            }
            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        HttpResponse<JsonNode> res = Unirest.post(API_BOT_URL + "/sendMessage")
                .field("chat_id", message.getChatId())
                .field("text", message.getText())
                .field("reply_markup", new ObjectMapper().writeValueAsString(inlineKeyboardMarkup))
                .asJson();
        System.out.println(res);
    }

    private void sendTextMsg(Message message) throws Exception {
        HttpResponse<JsonNode> res = Unirest.post(API_BOT_URL + "/sendMessage")
                .field("chat_id", message.getChatId())
                .field("text", message.getText())
                .asJson();
    }

    private static String getResource(ClassPathResource resource) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(resource.getURI())))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private static String getText(String key) {
        return maps.get(key);
    }

    private void setProxy() {
        Unirest.setProxy(new HttpHost("127.0.0.1", 10809));
    }
}
