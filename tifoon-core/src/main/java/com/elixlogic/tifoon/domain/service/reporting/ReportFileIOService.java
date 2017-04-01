package com.elixlogic.tifoon.domain.service.reporting;

public interface ReportFileIOService {
    void saveFile(String _filename, byte[] _data);

    void saveFileAsUTF8(String _filename, String _data);
}
