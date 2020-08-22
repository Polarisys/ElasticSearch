package com.web.utils;

import com.web.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Tolaris
 * @date 2020/4/13 - 23:57
 */
@Component
public class HtmlParseUtil {

    public List<Content> parseJD(String keywords) throws IOException {
        String url = "https://search.jd.com/Search?keyword=" + keywords+"&enc" +
                "=utf-8";
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");

        ArrayList<Content> list = new ArrayList<>();

        for (Element element1 : elements) {
            String img = element1.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = element1.getElementsByClass("p-price").eq(0).text();
            String title = element1.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setTitle(title);
            content.setPrice(price);
            list.add(content);
        }
        return list;

    }
}
