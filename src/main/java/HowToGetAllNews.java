import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
        //пролучаем адреса rss из

    }

    /**
     *
     * @return
     */
    private static Map<String, String> getSources(){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(
                            HowToGetAllNews.class.getResource("/config.yml"),
                            SourceList.class)
                    .getSources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SyndFeed feedFromUrl(String url) {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(TIMEOUT);
            XmlReader reader = new XmlReader(conn);
            return new SyndFeedInput().build(reader);
        } catch (IOException e) {
            log.warn("failed to connect - " + url + " skipped");
            return null;
        } catch (FeedException | NullPointerException e) {
            log.warn("failed to parse response from - " + url + " skipped");
            return null;
        }
    }


    static class SourceList{
        Map<String, String> sources;

        public Map<String, String> getSources() {
            return sources;
        }
    }

}
