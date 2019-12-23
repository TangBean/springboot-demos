package org.tangbean.springbucks.model;

import lombok.*;
import org.joda.money.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Document
public class Coffee implements Serializable {
    @Id
    private String id;

    private String name;

    private Money price;

    private Date createTime;

    private Date updateTime;
}
