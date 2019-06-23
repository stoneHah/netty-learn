package com.zq.learn.netty;

import org.junit.Test;

import java.util.Optional;

public class OptionalTest {

    @Test
    public void test(){
        Optional<String> hello = Optional.of("hello");
        hello.flatMap((value)->
            Optional.of(value.toUpperCase()));
    }
}
