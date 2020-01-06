package mainApp;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class UrlResponse {
    static final Double ADDS_ON_PAGES = 39.00;
    static Double numberOfAds;
    String jobName;

    public StringBuilder getContentBuilder() {
        return contentBuilder;
    }

    private StringBuilder contentBuilder;

    public UrlResponse(String jobName) {
        this.jobName = jobName;
        this.contentBuilder = getAllResponseHTMLCode(jobName);
    }

    private static StringBuilder getAllResponseHTMLCode(String jobName) {
        StringBuilder stringBuilder = getHTMLCode(jobName, 1);

        Double availablePages = Math.ceil(numberOfAds / ADDS_ON_PAGES) ;

        for (int i = 2; i <= availablePages; i++) {
            stringBuilder.append(getHTMLCode(jobName, i));
        }
        System.out.println("Available pages: " + availablePages);
        return stringBuilder;
    }

    public static StringBuilder getHTMLCode(String jobName, Integer pageNo) {
        WebClient webClient = new WebClient();
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage currentPage = null;
        try {
            currentPage = webClient.getPage("https://www.olx.pl/praca/q-" + jobName + "/?page=" + pageNo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        WebResponse response = currentPage.getWebResponse();
        String htmlCode = response.getContentAsString();
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("https://www.olx.pl/praca/q-" + jobName + "/?page=" + pageNo);
        contentBuilder.append("\r\n");
        contentBuilder.append(htmlCode);
        contentBuilder.append("\r\n");
        contentBuilder.append("koniec strony");

        if (pageNo == 1) {
            Document document = Jsoup.parse(htmlCode);
            String div = document.select("#offers_table > tbody > tr:nth-child(1) > td > div.hasPromoted.section.clr > p").text();
            numberOfAds = !div.isEmpty() ? Double.parseDouble(div.replaceAll("[\\D]", "")) : 0;
            System.out.println("number of ads: " + numberOfAds);
        }
        System.out.println("pageNo: " + pageNo);

        return contentBuilder;
    }
}
