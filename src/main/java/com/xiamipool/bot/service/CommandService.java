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
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.xiamipool.bot.constant.Constant.Command.AIRDROP;
import static com.xiamipool.bot.constant.Constant.Command.HELP;

@Service
public class CommandService {

    private final static String API_BOT_URL_TEST = "https://api.telegram.org/bot1813773806:AAF0D996Js7QxcX385js6uD2fpHIMkrJbEo";
    private final static String API_BOT_URL = "https://api.telegram.org/bot1707053096:AAE7yQ7cqSUB9xG6Ryev09UPPINxdLQe54E";

    private final static String GROUP_ID_TEST = "-522989399";
    private final static String GROUP_ID = "-522989399";

    private static Map<String, String> maps = new HashMap<>();

    static {
        maps.put("/airdrop", "You are now participating in the XiaMiPool airdrop!" +
                "For every ticket you will receive more $XMPT tokens in our airdrop. Refer your friends through your unique URL to receive more tickets.");

        maps.put("/doc", "*What is XiaMi-Fishing*" +
                "\n" +
                "   https://doc.xiamipool.com/\n" +
                "\n" +
                "*Ofiicial Webs:*\n" +
                "\n" +
                "    【BSC】\n" +
                "     https://bsc.xiamipool.com/\n" +
                "\n" +
                "    【HECO】\n" +
                "     https://heco.xiamipool.com\n" +
                "\n" +
                "*How to use bsc wallet*\n" +
                "\n" +
                "    【Metamask】\n" +
                "    https://docs.binance.org/smart-chain/wallet/metamask.html\n" +
                "    \n" +
                "    【Mathwallet】\n" +
                "    https://docs.binance.org/smart-chain/wallet/math.html\n" +
                "    \n" +
                "    【Trustwallet】\n" +
                "    https://docs.binance.org/smart-chain/wallet/trustwallet.html");

        maps.put("/token", "*Token Information Of XMPT*\n" +
                "    \n" +
                "    【XMPT BSC Contact Address】\n" +
                "    0x8099c0c7b3e530f563d4b121abd2ee365c72fb78\n" +
                "\n" +
                "    【Pancakeswap】\n" +
                "    https://exchange.pancakeswap.finance/#/swap?outputCurrency=0x8099c0c7b3e530f563d4b121abd2ee365c72fb78\n" +
                "    \n" +
                "    【Trading History】\n" +
                "    https://www.dextools.io/app/pancakeswap/pair-explorer/0xdcf0ccb215c854ff99900d608aad6a7d778e500d");
    }

    // 回调
    public boolean hook(String body) throws Exception {
        Message message = new Message();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject msg = jsonObject.getJSONObject("message");
        if (Objects.isNull(msg)) {
            System.out.println("hook not dealt with yet");
            return false;
        }

        String cmd = msg.getString("text");
        message.setChat(JSONObject.parseObject(msg.getString("chat"), Chat.class));
        String text = getText(cmd);
        if (StringUtils.isEmpty(text)) {
            text = "sorry, no such command!";
        }

        message.setText(text);
        if (AIRDROP.equals(cmd)) {
            return sendTextMsg(message);
        } else if (HELP.equals(cmd)) {
            return sendGraphicsMsg(message);
        } else {
            return sendTextMsg(message);
        }
    }

    // 文档/Token信息
    public void sendDoc(String cmd) {
        try {
            sendTextMsg(GROUP_ID, getText(cmd));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Unirest.setProxy(new HttpHost("127.0.0.1", 10809));
        HttpResponse<JsonNode> res = Unirest.post(API_BOT_URL + "/sendMessage")
                .field("chat_id", "-522989399")
                .field("parse_mode", "Markdown")
                .field("text", getText("/token"))
                .asJson();
    }

    private boolean sendGraphicsMsg(Message message) throws Exception {
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
        return 200 == res.getStatus();
    }

    private boolean sendTextMsg(Message message) throws Exception {
        HttpResponse<JsonNode> res = Unirest.post(API_BOT_URL + "/sendMessage")
                .field("chat_id", message.getChatId())
                .field("parse_mode", "Markdown")
                .field("text", message.getText())
                .asJson();
        return 200 == res.getStatus();
    }

    private boolean sendTextMsg(String chatId, String text) throws Exception {
        HttpResponse<JsonNode> res = Unirest.post(API_BOT_URL + "/sendMessage")
                .field("chat_id", chatId)
                .field("parse_mode", "Markdown")
                .field("text", text)
                .asJson();
        return 200 == res.getStatus();
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
