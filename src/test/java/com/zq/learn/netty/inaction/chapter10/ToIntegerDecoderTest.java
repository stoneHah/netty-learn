package com.zq.learn.netty.inaction.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/23
 **/
public class ToIntegerDecoderTest {

    @Test
    public void testToIntegerDecoder(){
        ByteBuf buf = Unpooled.buffer();
        for(int i = 0;i < 4;i++) {
            buf.writeInt(i);
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new ToIntegerDecoder());
        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        Object o = channel.readInbound();
        assertEquals(0,o);

        o = channel.readInbound();
        assertEquals(1,o);

        o = channel.readInbound();
        assertEquals(2,o);

        o = channel.readInbound();
        assertEquals(3,o);

        assertNull(channel.readInbound());

        buf.release();
    }

    @Test
    public void testToIntegerDecoder2(){
        ByteBuf buf = Unpooled.buffer();
        for(int i = 0;i < 4;i++) {
            buf.writeInt(i);
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new ToIntegerDecoder2());
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(14)));

        assertTrue(channel.finish());

        Object o = channel.readInbound();
        assertEquals(0,o);

        o = channel.readInbound();
        assertEquals(1,o);

        o = channel.readInbound();
        assertEquals(2,o);

        o = channel.readInbound();
        assertEquals(3,o);

        assertNull(channel.readInbound());

        ReferenceCountUtil.release(buf);
    }

    private byte[] intToBytes(int num) {
        byte[] result = new byte[4];

        result[0] = (byte) ((num >> 24) & 0xff);
        result[1] = (byte) ((num >> 16) & 0xff);
        result[2] = (byte) ((num >> 8) & 0xff);
        result[3] = (byte) (num & 0xff);

        return result;
    }

}