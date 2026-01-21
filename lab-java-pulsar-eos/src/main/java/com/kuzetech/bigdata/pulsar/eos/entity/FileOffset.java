package com.kuzetech.bigdata.pulsar.eos.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileOffset {
    private Long id;
    private String filePath;
    private Long fileSize;
    private Long offsetPosition;
    private Long totalLines;
    private Long processedLines;
    private String status;
    private String errorMessage;
    private Date createdAt;
    private Date updatedAt;
}
