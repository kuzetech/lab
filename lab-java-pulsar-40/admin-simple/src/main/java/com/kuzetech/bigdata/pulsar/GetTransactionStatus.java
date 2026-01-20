package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.AdminUtil;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.transaction.TxnID;
import org.apache.pulsar.common.policies.data.TransactionMetadata;

public class GetTransactionStatus {
    public static void main(String[] args) {
        try (PulsarAdmin admin = AdminUtil.createDefaultLocalAdmin()) {
            TransactionMetadata transactionMetadata = admin.transactions().getTransactionMetadata(new TxnID(1L, 1L));
            System.out.println("TransactionMetadata: " + transactionMetadata);
        } catch (PulsarClientException | PulsarAdminException e) {
            System.err.println("Failed to get TransactionMetadata info: " + e.getMessage());
        }
    }
}
