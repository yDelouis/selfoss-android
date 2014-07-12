package fr.ydelouis.selfoss.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.entity.Article;

public class ArticleContentParser {

    private Article article;

    public ArticleContentParser(Article article) {
        this.article = article;
    }

    public List<String> getImagesUrls() {
        List<String> imageUrls = new ArrayList<String>();
        Document document = Jsoup.parse(article.getContent());
        for (Element element : document.getElementsByTag("img")) {
            String src = element.attr("src");
            if (src != null && !src.isEmpty()) {
                imageUrls.add(src);
            }
        }
        return imageUrls;
    }

    public String getTitleOfImage(String imageSrc) {
        Document document = Jsoup.parse(article.getContent());
        for (Element element : document.getElementsByTag("img")) {
            if (imageSrc.equals(element.attr("src"))) {
                return element.attr("title");
            }
        }
        return null;
    }

	public String getContentWithoutImage() {
		String content = article.getContent();
		if (article.hasImage()) {
			Document document = Jsoup.parse(content);
			for (Element element : document.getElementsByTag("img")) {
				String src = element.attr("src");
				if (article.getImageUrl().equals(src)) {
					element.remove();
					document.outputSettings().charset("ISO-8859-1");
					content = document.html();
					break;
				}
			}
		}
		return content;
	}
}
