package com.summary.api.consumer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class HeadlineScrapper {

    private static final Logger log = LoggerFactory.getLogger(HeadlineScrapper.class);

    //    public static final String url = "https://www.channelnewsasia.com/news/singapore/man-hit-covid19-safe-distancing-ambassador-hougang-mcdonalds-12655216";
    public static final String url2 = "https://www.businessinsider.sg/coronavirus-antibodies-cant-guarantee-long-term-immunity-who-said-2020-4?r=US&IR=T";
    public static final String url3 = "https://www.todayonline.com/singapore/all-construction-workers-holding-work-permits-or-s-passes-be-placed-compulsory-stay-home";
    public static final String url4 = "https://www.channelnewsasia.com/news/asia/hong-kong-protests-police-arrest-more-than-200-12721380";
//    private static final String source = "Channelnewsasia.com";
//    private static final String source = "Todayonline.com";

    @Resource(name = "seleniumSources")
    private List<String> seleniumSources;

    @Resource(name = "jsoupSources")
    private List<String> jsoupSources;

    @Resource(name = "contentClassMap")
    private HashMap<String, String> contentClassMap;

    @Resource(name = "contentIdMap")
    private HashMap<String, String> contentIdMap;

    @Resource(name = "filterClassMap")
    private HashMap<String, List<String>> filterClassMap;

    @Resource(name = "filterTagMap")
    private HashMap<String, List<String>> filterTagMap;

//    private ScrapperReference scrapperReference;

//    @Autowired
//    public HeadlineScrapper(ScrapperReference scrapperReference) {
//        this.scrapperReference = scrapperReference;
//    }


    public String scrapeContent(String url, String source) {

        if (seleniumSources.contains(source)) {
            return scrapeWithSelenium(url, source);
        } else if (jsoupSources.contains(source)) {
            return scrapeWithJsoup(url, source);
        }

        return "failed";
    }

    private void filterUnnecessaryWords(HashMap<String, List<String>> filterClassMap,
                                        HashMap<String, List<String>> filterTagMap,
                                        Elements elements, String source) {
        if (filterClassMap.containsKey(source)) {
            List<String> filters = filterClassMap.get(source);
            filters.forEach(filter -> {
                elements.select("div." + filter).remove();
            });
        }
        if (filterTagMap.containsKey(source)) {
            List<String> tagFilters = filterTagMap.get(source);
            tagFilters.forEach(tagFilter -> {
                elements.select(tagFilter).remove();
            });
        }
        elements.select("aside[class*=advertisement]").remove();
        elements.select("div[class*=picture]").remove();
    }

    private String scrapeWithJsoup(String url, String source) {

        try {
            Document doc = Jsoup.connect(url).maxBodySize(0).timeout(30000).get();

            if (contentClassMap.containsKey(source)) {
                String content = contentClassMap.get(source);
                Elements elements = doc.getElementsByClass(content);
                filterUnnecessaryWords(filterClassMap, filterTagMap, elements, source);
                return Jsoup.parse(elements.text()).text();
            } else if (contentIdMap.containsKey(source)) {
                String id = contentIdMap.get(source);
                Elements elements = doc.select("div#" + id);
                filterUnnecessaryWords(filterClassMap, filterTagMap, elements, source);
                return Jsoup.parse(elements.text()).text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "failed";
    }

    private String scrapeWithSelenium(String url, String source) {

        String content;
        boolean isFindByClass = false;

        if (contentClassMap.containsKey(source)) {
            content = contentClassMap.get(source);
            isFindByClass = true;
        } else if (contentIdMap.containsKey(source)) {
            content = contentIdMap.get(source);
        } else {
            return "failed";
        }

        String result;
        boolean isFilterByClass = filterClassMap.containsKey(source);
        List<String> filters = new ArrayList<>();
        if (filterClassMap.containsKey(source)) {
            filters = filterClassMap.get(source);
            isFilterByClass = true;
        } else if (filterTagMap.containsKey(source)) {
            filters = filterTagMap.get(source);
            isFilterByClass = false;
        }

        try {
//            String GOOGLE_CHROME_PATH = System.getenv("GOOGLE_CHROME_BIN");
            String GOOGLE_CHROME_PATH = System.getenv("GOOGLE_CHROME_SHIM");
            String CHROMEDRIVER_PATH = System.getenv("CHROMEDRIVER_PATH");
            ChromeOptions options = new ChromeOptions();
//            options.addArguments("start-maximized"); // open Browser in maximized mode
            options.addArguments("disable-infobars"); // disabling infobars
            options.addArguments("--disable-extensions"); // disabling extensions
            options.addArguments("--disable-gpu"); // applicable to windows os only
            options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
            options.addArguments("--no-sandbox");
            options.addArguments("--headless");
            options.addArguments("--disable-default-apps");
            options.addArguments("--hide-scrollbars");
            options.addArguments("--disable-sync");
            options.addArguments("--disable-translate");
            options.addArguments("--blink-settings=imagesEnabled=false");
//            chromeOptions.addArguments("--no-sandbox'");
            options.setBinary(GOOGLE_CHROME_PATH);
//            chromeOptions.addArguments("--disable-gpu");
//            chromeOptions.addArguments("--disable-extensions");
//            chromeOptions.addArguments("--disable-dev-shm-usage");
            System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_PATH);
//            String GOOGLE_CHROME_PATH = "/app/.apt/usr/bin/google_chrome";
//            String CHROMEDRIVER_PATH = "/app/.chromedriver/bin/chromedriver";
            ChromeDriver driver = new ChromeDriver(options);
//            System.setProperty("webdriver.chrome.driver", "chromedriver");
//            ChromeDriver driver = new ChromeDriver();
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            log.info("Loading url.. resting for 6 seconds to load");
            driver.get(url);
            Thread.sleep(6000);
            driver.findElement(By.tagName("body")).click();
//            driver.findElement(By.tagName("body")).sendKeys(Keys.PAGE_DOWN);
            WebElement ele = driver.findElement(By.className(content));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ele);
            log.info("Scrolled down.. resting for 4 seconds to load");
            Thread.sleep(4000);
            if (isFindByClass) {
                log.info("Let it load content for 2s");
                result = driver.findElementByClassName(content).getText();
                Thread.sleep(2000);
            } else {
                log.info("Let it load content for 2s");
                result = driver.findElementById(content).getText();
                Thread.sleep(2000);
            }
            if (!filters.isEmpty()) {
                log.info("Let it load all elements for 2s");
                List<WebElement> webElements = driver.findElementByClassName(content).findElements(By.xpath(".//*"));
                Thread.sleep(2000);
//                    List<WebElement> webElements =  driver.findElements(By.xpath("//*[@id=\"node-article-news-article-group-column-1\"]/div[2]/*"));
                for (WebElement element : webElements) {
                    if (isFilterByClass) {
                        if (element.getAttribute("class") != null) {
                            if (filters.stream().parallel().anyMatch(element.getAttribute("class")::contains)) {
                                result = result.replaceFirst(element.getText(), "").trim();
                            }
                        }
                    } else {
                        if (filters.contains(element.getTagName())) {
                            result = result.replaceFirst(element.getText(), "").trim();
                        }
                    }
                    if(element.getTagName().equals("a")){
                        result = result.replaceFirst(element.getText(), "").trim();
                    }
                }
            }
            driver.quit();
            log.info("sleep for 3s to close resources");
            Thread.sleep(3000);
            return result.replaceAll("ADVERTISEMENT", "").trim();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed";
    }
}
