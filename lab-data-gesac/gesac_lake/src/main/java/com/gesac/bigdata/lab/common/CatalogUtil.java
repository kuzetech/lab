package com.gesac.bigdata.lab.common;

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.flink.FlinkCatalogFactory;
import org.apache.paimon.options.Options;

public class CatalogUtil {
    public static Catalog generateHiveCatalog() {
        Options catalogOptions = new Options();
        catalogOptions.set("type", "paimon");
        catalogOptions.set("metastore", "hive");
        catalogOptions.set("uri", "thrift://hive-metastore:9083");
        catalogOptions.set("hive-conf-dir", "./src/main/resources/hive-site.xml");
        catalogOptions.set("warehouse", "hdfs://namenode:9000/paimon/hive");
        return FlinkCatalogFactory.createPaimonCatalog(catalogOptions);
    }
}
