package org.tangbean.springbucks.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Document
public class CoffeeOrder implements Serializable {
    @Id
    private String id;

    private String customer;

    private List<Coffee> items;

    private OrderState state;

    private Date createTime;

    private Date updateTime;
}
