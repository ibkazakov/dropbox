package common.accepters;


import common.JSONSerializable.JSONAnswer;
import common.JSONSerializable.JSONCommand;
import com.alibaba.fastjson.JSON;
import common.JSONSerializable.JSONFileList;
import server.ServerAccepter;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

// getting json-string answering to singular command
public class CommandAccepter {

    private Path serverPath;

    private ServerAccepter uplink;

    public CommandAccepter(Path serverPath, ServerAccepter uplink) {
        this.serverPath = serverPath;
        this.uplink = uplink;
    }

    public String accept(JSONCommand command, String clientName) {
        switch (command.getType()) {
            case "get":
                return get(command, clientName);
            case "post":
                return post(command, clientName);
            case "create_dir":
                return create_dir(command, clientName);
            case "delete":
                return delete(command, clientName);
            case "move":
                return move(command, clientName);
            case "getfilelist":
                return getfileList(clientName);
            default:
                // unknown type of command
                return uncorrect(command, "no such command");
        }

    }

    // commands processing

    // still formal
    private String get(JSONCommand command, String clientName) {
        JSONAnswer answer = new JSONAnswer(command);

        try {
            Path filePath = pathResolve(command, clientName, 0);
            if (Files.notExists(filePath)) {
                answer.setError("required file not exist");
            }
            else {


                //register file in FileAccepter. Return the fileID in answer
                int fileID = uplink.newSendingFile(filePath);
                answer.setFileID(fileID);

                answer.setError("file found");
                answer.setSuccess(true);
            }
        }
        catch (InvalidPathException e) {
            return e.getInput();
        }
        catch (Exception e) {
            answer.setError("unknown error");
        }

        return JSON.toJSONString(answer);
    }

    private String post(JSONCommand command, String clientName) {
        JSONAnswer answer = new JSONAnswer(command);

        try {
            Path newFilePath = pathResolve(command, clientName, 0);
            if (Files.exists(newFilePath)) {
                answer.setError("file already exists");
            } else {
                // formal creating empty file
                Files.createFile(newFilePath);
                answer.setError("empty file created");
                answer.setSuccess(true);
                // register file in FilePartAccepter! We need also to return fileID descriptor in answer!:
                int fileID = uplink.newAcceptingFile(newFilePath);
                answer.setFileID(fileID);
            }
        }
        catch (InvalidPathException e) {
            return e.getInput();
        }
        catch (Exception e) {
            answer.setError("unknown error");
        }

        return JSON.toJSONString(answer);
    }

    private String create_dir(JSONCommand command, String clientName) {

        JSONAnswer answer = new JSONAnswer(command);

        try {
            Path newDirPath = pathResolve(command, clientName, 0);

            if (Files.exists(newDirPath)) {
                answer.setError("already exists");
            } else {
                Files.createDirectory(newDirPath);
                answer.setError("created");
                answer.setSuccess(true);
            }
        }
        catch (InvalidPathException e) {
            return e.getInput();
        }
        catch (Exception e) {
            answer.setError("unknown error");
        }

        return JSON.toJSONString(answer);
    }

    private String delete(JSONCommand command, String clientName) {

        JSONAnswer answer = new JSONAnswer(command);

        //deltree
        try {
            Path filePath = pathResolve(command, clientName, 0);
            if (Files.notExists(filePath)) throw new NoSuchFileException(filePath.toString());
            if (Files.isDirectory(filePath)) {
                Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                });
            } else {
                Files.delete(filePath);
            }
            answer.setSuccess(true);
            answer.setError("deleted");
        }
        catch (InvalidPathException e) {
            return e.getInput();
        }
        catch (NoSuchFileException e) {
            answer.setError("file not found");
        }
        catch (Exception e) {
            answer.setError("unknown error");
        }

        return JSON.toJSONString(answer);
    }

    private String move(JSONCommand command, String clientName) {

        JSONAnswer answer = new JSONAnswer(command);

        try {
            Path fromPath = pathResolve(command, clientName, 0);
            Path toPath =  pathResolve(command, clientName, 1);

            if (Files.notExists(fromPath)) {
                answer.setError("file not found");
            }
            else if (Files.exists(toPath)) {
                answer.setError("trying to rewrite already existing file");
                }
                else {
                    Files.move(fromPath, toPath);
                    answer.setError("moved");
                    answer.setSuccess(true);
                }
            }
        catch (InvalidPathException e) {
            return e.getInput();
            }
        catch (Exception e) {
            answer.setError("unknown error");
        }

        return JSON.toJSONString(answer);
    }

    private String getfileList(String clientName) {

        JSONFileList fileList = new JSONFileList();

        try {
            Files.walkFileTree(serverPath.resolve(clientName), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fileList.addFilePath(serverPath.relativize(file));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    fileList.addDirectoryPath(serverPath.relativize(dir));
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (Exception e) {
            // unsuccessful file walking?
        }

        fileList.setSuccess(true);
        return JSON.toJSONString(fileList);
    }

    //uncorrect command
    private String uncorrect(JSONCommand command, String error) {
        JSONAnswer answer = new JSONAnswer(command);
        answer.setError(error);
        return JSON.toJSONString(answer);
    }

    // resolving with correctness checking
    // if path is invalid, throw exception with JSON-coded answer message
    private Path pathResolve(JSONCommand command, String clientName, int paramIndex) throws InvalidPathException {
        try {
            Path resolvedPath = serverPath.resolve(clientName).resolve(command.getParams()[paramIndex]);
            pathCheck(resolvedPath, clientName);
            return resolvedPath;
        }
        catch (IndexOutOfBoundsException e) {
             throw new InvalidPathException(uncorrect(command, "lack of parameters"), "");
        }
        catch (NullPointerException e) {
            throw new InvalidPathException(uncorrect(command, "null params"), "");
        }
        catch (IllegalArgumentException e) {
            throw new InvalidPathException(uncorrect(command, "uncorrect path"), "");
        }
        catch (Exception e) {
            throw new InvalidPathException(uncorrect(command, "unknown error"), "");
        }
    }

    private void pathCheck(Path path, String clientName) throws IllegalArgumentException {
        // presuppose that clientName directory exists! This must be controlled by command giver.
        Path clientDirectory = serverPath.resolve(clientName).normalize().toAbsolutePath();
        Path filePath = path.normalize().toAbsolutePath();
        if (!filePath.startsWith(clientDirectory) || filePath.equals(clientDirectory)) {
            throw new IllegalArgumentException();
        }
    }

}
