package com.tilak.pagepulse.service;

import com.tilak.pagepulse.dto.AuditRequest;
import com.tilak.pagepulse.dto.AuditResponse;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuditService {

    public AuditResponse auditWebsite(AuditRequest request) {

        try {

            long startTime = System.currentTimeMillis();

            Connection connection = Jsoup.connect(request.getUrl())
                    .timeout(5000);

            Connection.Response response = connection.execute();

            Document document = response.parse();

            long endTime = System.currentTimeMillis();

            long responseTime = endTime - startTime;

            int status = response.statusCode();

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

                if (!image.hasAttr("alt") ||
                        image.attr("alt").trim().isEmpty()) {

                    missingAltImages++;
                }
            }

            String bodyText = document.body().text();

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

        }

        catch (IOException e) {

            return new AuditResponse(
                    0,
                    0,
                    "",
                    "Unable to reach the website.",
                    0,
                    0,
                    0
            );
        }
    }
}