package com.tilak.pagepulse.service;

import com.tilak.pagepulse.dto.AuditRequest;
import com.tilak.pagepulse.dto.AuditResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;

@Service
public class AuditService {

    public AuditResponse auditWebsite(AuditRequest request) {

        String url = request.getUrl().trim();

        // Add https:// if user enters github.com instead of https://github.com
        if (!url.matches("^(http://|https://).+")) {
            url = "https://" + url;
        }

        // Validate URL
        try {
            URI uri = new URI(url);

            if (uri.getHost() == null || !uri.getHost().contains(".")) {
                return errorResponse("Invalid URL. Please enter a valid website URL.");
            }

        } catch (Exception e) {
            return errorResponse("Invalid URL. Please enter a valid website URL.");
        }

        try {

            long startTime = System.currentTimeMillis();

            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0 Safari/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .timeout(15000);

            Connection.Response response = connection.execute();

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            int status = response.statusCode();

            // Reject HTTP errors
            if (status >= 400) {
                return new AuditResponse(
                        status,
                        responseTime,
                        "",
                        "Website returned HTTP " + status,
                        0,
                        0,
                        0
                );
            }

            // Reject PDFs, Images, JSON, ZIPs, etc.
            String contentType = response.contentType();

            if (contentType == null ||
                    !contentType.toLowerCase().contains("text/html")) {

                return new AuditResponse(
                        status,
                        responseTime,
                        "",
                        "The provided URL is not an HTML webpage.",
                        0,
                        0,
                        0
                );
            }

            Document document = response.parse();

            String title = document.title();

            String metaDescription = "";

            Element meta = document.selectFirst("meta[name=description]");

            if (meta != null) {
                metaDescription = meta.attr("content");
            }

            int h1Count = document.select("h1").size();

            int missingAltImages = 0;

            Elements images = document.select("img");

            for (Element image : images) {
                if (!image.hasAttr("alt") || image.attr("alt").trim().isEmpty()) {
                    missingAltImages++;
                }
            }

            String bodyText = document.body() != null
                    ? document.body().text()
                    : "";

            int wordCount = bodyText.isBlank()
                    ? 0
                    : bodyText.split("\\s+").length;

            return new AuditResponse(
                    status,
                    responseTime,
                    title,
                    metaDescription,
                    h1Count,
                    missingAltImages,
                    wordCount
            );

        } catch (SocketTimeoutException e) {

            return errorResponse("The website took too long to respond.");

        } catch (UnknownHostException e) {

            return errorResponse("Website not found.");

        } catch (IOException e) {

            return errorResponse("Unable to reach the website.");

        } catch (Exception e) {

            return errorResponse("Unexpected error while auditing the website.");
        }
    }

    private AuditResponse errorResponse(String message) {

        return new AuditResponse(
                0,
                0,
                "",
                message,
                0,
                0,
                0
        );
    }
}