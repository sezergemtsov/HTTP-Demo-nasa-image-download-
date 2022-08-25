import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    protected static ObjectMapper oMapper = new ObjectMapper();
    protected static String server = "https://api.nasa.gov/planetary/apod?api_key=IHiJhZLfEq0FgGHE5jPgAQf2SzbUBbJcgMF3cgMT";

    public static void main(String[] args) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(server);
            httpGet.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                NASAresp nasa = oMapper.readValue(
                        response.getEntity().getContent(),
                        new TypeReference<>() {}
                );
                HttpGet httpGet1 = new HttpGet(nasa.getHdurl());
                httpGet1.setHeader(HttpHeaders.ACCEPT, ContentType.IMAGE_JPEG);
                String [] uriName = nasa.getHdurl().split("/");
                try (CloseableHttpResponse response1 = httpclient.execute(httpGet1)) {
                    File img = new File("nasa_image " + uriName[uriName.length-1]);
                    if (img.createNewFile()) {
                        FileOutputStream fos = new FileOutputStream(img);
                        fos.write(response1.getEntity().getContent().readAllBytes());
                        fos.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
