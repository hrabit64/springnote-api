package com.springnote.api.utils.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

/**
 * Markdown stirng에 대해 각종 변환을 제공하는 클래스
 *
 * @author 황준서(' hzser123 @ gmail.com ')
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MarkdownHelper {

    /**
     * Markdown string을 HTML string으로 변환
     *
     * @param markdownString 변환할 Markdown string
     * @return 변환된 HTML string
     */
    public String toPlainText(String markdownString) {
        var options = new MutableDataSet();
        var parser = Parser.builder(options).build();
        var renderer = HtmlRenderer.builder(options).build();
        var document = parser.parse(markdownString);
        var htmlText = renderer.render(document);
        var plainText = Jsoup.parse(htmlText).text();
        var result = plainText.replaceAll("[^가-힣a-zA-Z0-9.\\s]", "");


        return result;
    }

}
