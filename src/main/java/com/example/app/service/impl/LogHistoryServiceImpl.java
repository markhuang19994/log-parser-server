package com.example.app.service.impl;

import com.example.app.model.LogDetail;
import com.example.app.service.LogHistoryService;
import com.example.app.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/27/19, MarkHuang,new
 * </ul>
 * @since 10/27/19
 */
@Service
public class LogHistoryServiceImpl implements LogHistoryService {
    private File historyDir;
    private AtomicInteger historyCount = new AtomicInteger(0);
    private ObjectMapper om = new ObjectMapper();

    public LogHistoryServiceImpl() {
        historyDir = FileUtil.newRandomTempDir("log_history");
        historyDir.deleteOnExit();
    }

    @Override
    public void writeHistory(List<LogDetail> logDetails) throws IOException {
        File historyFile = new File(historyDir, "log_detail_" + historyCount.getAndAdd(1) + ".h.gz");
        try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(historyFile))) {
            String logDetailsStr = om.writeValueAsString(logDetails);
            gos.write(logDetailsStr.getBytes());
            gos.flush();
        }
    }

    @Override
    public List<LogDetail> readHistory(int index) throws IOException {
        String pattern = "^log_detail_(\\d+)\\.h\\.gz";
        File[] historyFiles = historyDir.listFiles(file -> file.getName().matches(pattern));

        if (historyFiles == null) {
            throw new RuntimeException("There are no history files at:" + historyDir.getAbsolutePath());
        }

        Arrays.sort(historyFiles, (f1, f2) -> {
            int f1Idx = Integer.parseInt(f1.getName().replaceAll(pattern, "$1"));
            int f2Idx = Integer.parseInt(f2.getName().replaceAll(pattern, "$1"));
            return Integer.compare(f1Idx, f2Idx);
        });

        if (index < 0) {
            index = historyCount.get() - 1 + index;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPInputStream gis = new GZIPInputStream(new FileInputStream(historyFiles[index]))) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = gis.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
            String logDetailsStr = baos.toString();
            List<LogDetail> logDetailList = om.readValue(logDetailsStr, new TypeReference<List<LogDetail>>() {
            });
            writeHistory(logDetailList);
            return logDetailList;
        }
    }


}
