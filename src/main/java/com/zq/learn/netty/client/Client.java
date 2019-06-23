package com.zq.learn.netty.client;

/**
 * @author qun.zheng
 * @description: TODO
 * @date 2019-06-2311:15
 */
public interface Client {
    String getHost();

    int getPort();

    void start() throws Exception;
}
