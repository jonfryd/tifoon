package com.elixlogic.tifoon.domain.service.reporting.impl;

import com.elixlogic.tifoon.domain.service.reporting.ReportFileIOService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ReportFileIOServiceImpl implements ReportFileIOService {
    @Override
    public void saveFile(@NonNull final String _filename,
                         @NonNull final byte[] _data) {
        try {
            FileUtils.writeByteArrayToFile(new File(_filename), _data);
        } catch (IOException _e) {
            log.error("Failed to save report file", _e);
        }
    }

    @Override
    public void saveFileAsUTF8(@NonNull final String _filename,
                               @NonNull final String _data) {
        try {
            FileUtils.write(new File(_filename), _data, StandardCharsets.UTF_8);
        } catch (IOException _e) {
            log.error("Failed to save report file", _e);
        }
    }
}
