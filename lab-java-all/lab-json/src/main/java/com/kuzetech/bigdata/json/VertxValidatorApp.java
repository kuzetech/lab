package com.kuzetech.bigdata.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.json.schema.*;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.common.dsl.Schemas;

import java.io.InputStream;

public class VertxValidatorApp {
    public static void main(String[] args) {

        ObjectSchemaBuilder objectSchemaBuilder =
                Schemas.objectSchema()
                        .requiredProperty("id", Schemas.intSchema())
                        .requiredProperty("name", Schemas.stringSchema())
                        .requiredProperty("money", Schemas.numberSchema())
                        .allowAdditionalProperties(false);

        JsonSchema schema = JsonSchema.of(objectSchemaBuilder.toJson());

        JsonSchemaOptions jsonSchemaOptions = new JsonSchemaOptions()
                .setDraft(Draft.DRAFT4)
                .setBaseUri("https://vertx.io");

        Validator validator = Validator.create(schema, jsonSchemaOptions);

        Object dataNode;
        try (InputStream inputStream = VertxValidatorApp.class.getClassLoader().getResourceAsStream("data-student-err.json")) {
            assert inputStream != null;
            dataNode = Json.decodeValue(Buffer.buffer(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OutputUnit result = validator.validate(dataNode);

        System.out.println(result.getValid());
    }
}
