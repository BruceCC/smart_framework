package org.leave.framework.bean;

import java.io.InputStream;

public class FileParam {
    private String fileName;
    private String fieldName;
    private long fileSize;
    private String contentType;
    private InputStream inputstream;

    public FileParam(String fileName, String fieldName, long fileSize, String contentType, InputStream inputstream) {
        this.fileName = fileName;
        this.fieldName = fieldName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.inputstream = inputstream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getInputstream() {
        return inputstream;
    }

    public void setInputstream(InputStream inputstream) {
        this.inputstream = inputstream;
    }
}
