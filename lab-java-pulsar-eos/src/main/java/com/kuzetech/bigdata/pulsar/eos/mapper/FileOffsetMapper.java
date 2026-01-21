package com.kuzetech.bigdata.pulsar.eos.mapper;

import com.kuzetech.bigdata.pulsar.eos.entity.FileOffset;
import org.apache.ibatis.annotations.*;

public interface FileOffsetMapper {

    @Select("SELECT * FROM file_offsets WHERE file_path = #{filePath}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "fileSize", column = "file_size"),
            @Result(property = "offsetPosition", column = "offset_position"),
            @Result(property = "totalLines", column = "total_lines"),
            @Result(property = "processedLines", column = "processed_lines"),
            @Result(property = "status", column = "status"),
            @Result(property = "errorMessage", column = "error_message"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    FileOffset selectByFilePath(@Param("filePath") String filePath);

    @Insert("INSERT INTO file_offsets (file_path, file_size, offset_position, total_lines, processed_lines, status) " +
            "VALUES (#{filePath}, #{fileSize}, #{offsetPosition}, #{totalLines}, #{processedLines}, #{status}) " +
            "ON DUPLICATE KEY UPDATE file_size = #{fileSize}, status = 'PROCESSING', updated_at = CURRENT_TIMESTAMP")
    int insertOrUpdate(FileOffset fileOffset);

    @Update("UPDATE file_offsets SET offset_position = #{offsetPosition}, processed_lines = #{processedLines}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE file_path = #{filePath}")
    int updateOffset(@Param("filePath") String filePath,
                     @Param("offsetPosition") Long offsetPosition,
                     @Param("processedLines") Long processedLines);

    @Update("UPDATE file_offsets SET status = 'COMPLETED', total_lines = #{totalLines}, " +
            "processed_lines = #{totalLines}, updated_at = CURRENT_TIMESTAMP WHERE file_path = #{filePath}")
    int markCompleted(@Param("filePath") String filePath, @Param("totalLines") Long totalLines);

    @Update("UPDATE file_offsets SET status = 'FAILED', error_message = #{errorMessage}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE file_path = #{filePath}")
    int markFailed(@Param("filePath") String filePath, @Param("errorMessage") String errorMessage);
}
