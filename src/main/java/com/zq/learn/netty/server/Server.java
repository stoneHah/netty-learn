package com.zq.learn.netty.server;

/**
 * @author qun.zheng
 * @description: TODO
 * @date 2019-06-2208:22
 */
public interface Server {

    /**
     *
     * @return
     */
    int getPort();
    /**
     * server to start
     */
    void start() throws Exception;
}
