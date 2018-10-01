package common.handler_coders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

// '\n' delimiter

// see also
// https://stackoverflow.com/questions/38382754/in-netty-how-we-can-send-string-messages-with-different-arbitrary-length

public class MyStringDecoder extends ByteToMessageDecoder {

    private StringBuilder builder = new StringBuilder();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {// (2)
           while (in.readableBytes() > 1) {
               char currentChar = in.readChar();
               if (currentChar == '\n') {
                   out.add(builder.toString());
                   builder.setLength(0);
               } else {
                   builder.append(currentChar);
               }
           }
    }

}
