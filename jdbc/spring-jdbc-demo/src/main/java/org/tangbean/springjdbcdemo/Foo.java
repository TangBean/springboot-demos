package org.tangbean.springjdbcdemo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Foo {
    private Long id;
    private String bar;
}
