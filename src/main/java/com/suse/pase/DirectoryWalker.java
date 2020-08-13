package com.suse.pase;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

/** Utils to walk directory trees */
public class DirectoryWalker {

    /** Calls a method for all text files in a directory */
    public static void forEachTextFileIn(Path path, Consumer<Path> consumer) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                var type = isText(path);
                if (type) {
                    consumer.accept(path);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static byte[] buf = new byte[4096];

    private static boolean isText(Path path) throws IOException {
        // same heuristic used by diff
        // https://dev.to/sharkdp/what-is-a-binary-file-2cf5
        try (var file = new RandomAccessFile(path.toFile(), "r")) {
            var count = file.read(buf, 0, buf.length);
            for (int i = 0; i < count; i++) {
                if (buf[i] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}