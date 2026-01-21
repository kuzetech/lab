package com.kuzetech.bigdata.pulsar.eos.mapper;

import com.kuzetech.bigdata.pulsar.eos.entity.TransactionLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface TransactionLogMapper {

    @Insert("INSERT INTO transaction_log (file_path, transaction_id, start_offset, end_offset, message_count, status) " +
            "VALUES (#{filePath}, #{transactionId}, #{startOffset}, #{endOffset}, #{messageCount}, #{status})")
    int insert(TransactionLog transactionLog);

    @Update("UPDATE transaction_log SET status = #{status}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE transaction_id = #{transactionId}")
    int updateStatus(@Param("transactionId") String transactionId,
                     @Param("status") TransactionLog.TransactionStatus status);

    @Select("SELECT * FROM transaction_log WHERE transaction_id = #{transactionId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "transactionId", column = "transaction_id"),
            @Result(property = "startOffset", column = "start_offset"),
            @Result(property = "endOffset", column = "end_offset"),
            @Result(property = "messageCount", column = "message_count"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    TransactionLog selectByTransactionId(@Param("transactionId") String transactionId);

    @Select("SELECT * FROM transaction_log WHERE file_path = #{filePath} AND status = 'PREPARED' " +
            "ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "transactionId", column = "transaction_id"),
            @Result(property = "startOffset", column = "start_offset"),
            @Result(property = "endOffset", column = "end_offset"),
            @Result(property = "messageCount", column = "message_count"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<TransactionLog> selectPreparedByFilePath(@Param("filePath") String filePath);
}
