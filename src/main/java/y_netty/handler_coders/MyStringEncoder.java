package y_netty.handler_coders;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyStringEncoder extends MessageToByteEncoder<String> {

    // Allocate a ByteBuf which will be used as argument of #encode(ChannelHandlerContext, I, ByteBuf).
    // Sub-classes may override this method to return ByteBuf with a perfect matching initialCapacity.
    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, String msg, boolean preferDirect) throws Exception {
        int capacity = msg.length() * 2 + 2; // msg + '\n'. Two bytes per char
        return Unpooled.directBuffer(capacity);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        for(int i = 0; i < msg.length(); i++) {
            out.writeChar(msg.charAt(i));
        }
        out.writeChar('\n');
    }
}
