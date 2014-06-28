package fr.ydelouis.selfoss.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import fr.ydelouis.selfoss.entity.Article;

public class ArticleContentParser {

    private Article article;

    public ArticleContentParser(Article article) {
        this.article = article;
    }

    public String extractImage() {
        Document document = Jsoup.parse(article.getContent());
        for (Element element : document.getElementsByTag("img")) {
            String src = element.attr("src");
            if (src != null && !src.isEmpty()) {
                return src;
            }
        }
        return null;
    }

    public String extractTitleOfImage(String imageSrc) {
        Document document = Jsoup.parse(article.getContent());
        for (Element element : document.getElementsByTag("img")) {
            if (imageSrc.equals(element.attr("src"))) {
                return element.attr("title");
            }
        }
        return null;
    }
}
