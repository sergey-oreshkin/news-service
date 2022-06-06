import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HowToGetAllNews {

    public static void main(String[] args) {

        HowToGetAllNews app = new HowToGetAllNews();

        //пролучаем адреса rss из файла с ресурсами
        Map<String, String> sources = app.getSources();

        //берем например yandex и получаем из него SyndFeed
        SyndFeed feed = app.feedFromUrl(sources.get("yandex"));

        //в фиде содержится коллекция FeedEntry, получим ее
        if (feed != null) {
            // к сожалению метод getEntries возвоащает коллекцию Object'ов
            // поэтому возможно где то понадобится привидение
            List<SyndEntry> entries = feed.getEntries();

            // выведем у первой полученной новости заголовок и описание
            SyndEntry entry = entries.get(0);
            System.out.println(entry.getTitle());
            System.out.println(entry.getDescription().getValue());
        }
    }

    /**
     * Считывает из yaml файла адреса rss и возвращает их как значения Map
     */
    private Map<String, String> getSources() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(
                            HowToGetAllNews.class.getResource("/sources.yml"),
                            SourceList.class)
                    .getSources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получаем объект SyndFeed загружая rss из переданного url
     *
     * @param url
     * @return SyndFeed - объект из пакета rome
     */
    private SyndFeed feedFromUrl(String url) {
        final int TIMEOUT = 1000;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(TIMEOUT);
            XmlReader reader = new XmlReader(conn); // reader из пакета rome
            return new SyndFeedInput().build(reader);
        } catch (IOException e) {
            System.out.println("failed to connect - " + url + " skipped");
            return null;
        } catch (FeedException | NullPointerException e) {
            System.out.println("failed to parse response from - " + url + " skipped");
            return null;
        }
    }

    /**
     * Вспомогательный класс для десериализации из yaml файла
     */
    static class SourceList {
        Map<String, String> sources;

        public Map<String, String> getSources() {
            return sources;
        }
    }
}
