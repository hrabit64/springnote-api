package com.springnote.api.utils.markdown;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import lombok.RequiredArgsConstructor;

/**
 * Markdown stirng에 대해 각종 변환을 제공하는 클래스
 * 
 * @author 황준서('hzser123@gmail.com')
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Component
public class MarkdownHelper {

    public String toHtml(String markdownString){
        var options = new MutableDataSet();
        var parser = Parser.builder(options).build();
        var renderer = HtmlRenderer.builder(options).build();
        var document = parser.parse(markdownString);

        return renderer.render(document);
    }

    public String toPlainString(String text, boolean isHtml){
        if(!isHtml){
            text = toHtml(text);
        }
        return Jsoup.parse(text).text();
    }

}
