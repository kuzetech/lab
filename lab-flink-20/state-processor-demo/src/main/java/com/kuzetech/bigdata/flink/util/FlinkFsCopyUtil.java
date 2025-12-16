package com.kuzetech.bigdata.flink.util;

import org.apache.flink.core.fs.*;

import java.io.IOException;

public class FlinkFsCopyUtil {
    public static void copyDirIfNotExists(
            Path srcDir,
            Path destDir
    ) throws IOException {

        FileSystem fs = srcDir.getFileSystem();

        for (FileStatus status : fs.listStatus(srcDir)) {
            Path srcPath = status.getPath();
            Path destPath = new Path(destDir, srcPath.getName());
            copyFileIfNotExists(fs, srcPath, destPath);
        }
    }

    /**
     * 拷贝单个文件（目标存在则跳过）
     */
    private static void copyFileIfNotExists(
            FileSystem fs,
            Path src,
            Path dest
    ) throws IOException {
        if (fs.exists(dest)) {
            return;
        }
        
        try (
                FSDataInputStream in = fs.open(src);
                FSDataOutputStream out = fs.create(dest, false)
        ) {
            byte[] buffer = new byte[64 * 1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }
}
