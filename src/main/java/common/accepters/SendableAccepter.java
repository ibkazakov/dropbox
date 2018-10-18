package common.accepters;

import java.nio.file.Path;

public interface SendableAccepter {
    void sendString(String jsonString);
}
