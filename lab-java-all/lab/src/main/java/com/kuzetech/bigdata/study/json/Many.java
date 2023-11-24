package com.kuzetech.bigdata.study.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Many {
    public static void main(String[] args) throws JsonProcessingException {

        String content = "{\"event\":\"login\",\"date\":-7.51999998092651}";

        String content1 = "{\n" +
                "  \"glossary\": {\n" +
                "    \"title\": \"example glossary\",\n" +
                "    \"GlossDiv\": {\n" +
                "      \"title\": \"S\",\n" +
                "      \"GlossList\": {\n" +
                "        \"GlossEntry\": {\n" +
                "          \"ID\": \"----\",\n" +
                "          \"SortAs\": \"SGML\",\n" +
                "          \"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                "          \"Acronym\": \"SGML\",\n" +
                "          \"Abbrev\": \"ISO 8879:1986\",\n" +
                "          \"GlossDef\": {\n" +
                "            \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
                "            \"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
                "          },\n" +
                "          \"GlossSee\": \"markup\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String content2 = "{\n" +
                "  \"#event\": \"unet_wolf_gather_log\",\n" +
                "  \"#log_id\": \"8829419535463054214\",\n" +
                "  \"#sdk_type\": \"ingest-client\",\n" +
                "  \"#sdk_version\": \"1.5.2\",\n" +
                "  \"#time\": 1698835804000,\n" +
                "  \"game_area\": \"CN\",\n" +
                "  \"gmod\": 111,\n" +
                "  \"mmod\": 41,\n" +
                "  \"pid\": 1247470463,\n" +
                "  \"platform_id\": 0,\n" +
                "  \"round\": 3,\n" +
                "  \"show_gmod\": 5,\n" +
                "  \"time\": 1698835804,\n" +
                "  \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\",\n" +
                "  \"#data_lifecycle\": \"0\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(content, ObjectNode.class);


        long begin = System.currentTimeMillis();

        mapper.readValue(content1, ObjectNode.class);
        mapper.readValue(content2, ObjectNode.class);

        long sub = System.currentTimeMillis() - begin;
        System.out.println(sub);


    }
}
