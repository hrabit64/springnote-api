package com.springnote.api.utils.image;


public record ImageResult(String originalFormat, String targetFormat, int width, int height,
                          String name) {

}