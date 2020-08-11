package com.ffzs.webflux.r2mongo_test.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: ffzs
 * @Date: 2020/8/11 上午9:15
 */
@Data
@Document(collection = "article")  // mongodb映射表
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class Article {

    @Id
    private Long id;
    private String author;
    private String title;
    private String text;
}
