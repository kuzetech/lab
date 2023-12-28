package com.kuzetech.bigdata.lab.json;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

public class Delete2 {

    public static void main(String[] args) throws JsonProcessingException {
        String content = "{\n" +
                "  \"#event\": \"unet_wolf_gather_log\",\n" +
                "  \"#log_id\": \"8829419535463054214\",\n" +
                "  \"#sdk_type\": \"ingest-client\",\n" +
                "  \"#sdk_version\": \"1.5.2\",\n" +
                "  \"#time\": 1698835804000,\n" +
                "  \"#user_id\": \"kmpmdb\",\n" +
                "  \"__id__\": \"8829419535463054214\",\n" +
                "  \"chest_list\": [\n" +
                "    {\n" +
                "      \"__id__\": \"8829419535463054214-1\",\n" +
                "      \"chest_id\": 0,\n" +
                "      \"chest_location_x\": -7.51999998092651,\n" +
                "      \"chest_location_z\": 10.8000001907349,\n" +
                "      \"chest_num\": 0,\n" +
                "      \"create_time\": 1693037962,\n" +
                "      \"custom_room_id\": 1082289,\n" +
                "      \"gmod\": 17,\n" +
                "      \"is_get\": 1,\n" +
                "      \"mmod\": 41,\n" +
                "      \"pid\": 1247470463,\n" +
                "      \"round\": 3,\n" +
                "      \"show_gmod\": 5,\n" +
                "      \"time\": 1698835804,\n" +
                "      \"tmod\": 1,\n" +
                "      \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"__id__\": \"8829419535463054214-2\",\n" +
                "      \"chest_id\": 1,\n" +
                "      \"chest_location_x\": -30.7600002288818,\n" +
                "      \"chest_location_z\": -24.7000007629395,\n" +
                "      \"chest_num\": 0,\n" +
                "      \"create_time\": 1693037962,\n" +
                "      \"custom_room_id\": 1082289,\n" +
                "      \"gmod\": 17,\n" +
                "      \"is_get\": 0,\n" +
                "      \"mmod\": 41,\n" +
                "      \"pid\": 1247470463,\n" +
                "      \"round\": 3,\n" +
                "      \"show_gmod\": 5,\n" +
                "      \"time\": 1698835804,\n" +
                "      \"tmod\": 1,\n" +
                "      \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"__id__\": \"8829419535463054214-3\",\n" +
                "      \"chest_id\": 2,\n" +
                "      \"chest_location_x\": -38.1500015258789,\n" +
                "      \"chest_location_z\": 25.2399997711182,\n" +
                "      \"chest_num\": 1,\n" +
                "      \"create_time\": 1693037962,\n" +
                "      \"custom_room_id\": 1082289,\n" +
                "      \"gmod\": 17,\n" +
                "      \"is_get\": 0,\n" +
                "      \"mmod\": 41,\n" +
                "      \"pid\": 1247470463,\n" +
                "      \"round\": 3,\n" +
                "      \"show_gmod\": 5,\n" +
                "      \"time\": 1698835804,\n" +
                "      \"tmod\": 1,\n" +
                "      \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"__id__\": \"8829419535463054214-4\",\n" +
                "      \"chest_id\": 3,\n" +
                "      \"chest_location_x\": 35.5299987792969,\n" +
                "      \"chest_location_z\": -12.5900001525879,\n" +
                "      \"chest_num\": 1,\n" +
                "      \"create_time\": 1693037962,\n" +
                "      \"custom_room_id\": 1082289,\n" +
                "      \"gmod\": 17,\n" +
                "      \"is_get\": 0,\n" +
                "      \"mmod\": 41,\n" +
                "      \"pid\": 1247470463,\n" +
                "      \"round\": 3,\n" +
                "      \"show_gmod\": 5,\n" +
                "      \"time\": 1698835804,\n" +
                "      \"tmod\": 1,\n" +
                "      \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"__id__\": \"8829419535463054214-5\",\n" +
                "      \"chest_id\": 4,\n" +
                "      \"chest_location_x\": -0.25,\n" +
                "      \"chest_location_z\": 24.3299999237061,\n" +
                "      \"chest_num\": 2,\n" +
                "      \"create_time\": 1693037962,\n" +
                "      \"custom_room_id\": 1082289,\n" +
                "      \"gmod\": 17,\n" +
                "      \"is_get\": 0,\n" +
                "      \"mmod\": 41,\n" +
                "      \"pid\": 1247470463,\n" +
                "      \"round\": 3,\n" +
                "      \"show_gmod\": 5,\n" +
                "      \"time\": 1698835804,\n" +
                "      \"tmod\": 1,\n" +
                "      \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"__id__\": \"8829419535463054214-6\",\n" +
                "      \"chest_id\": 5,\n" +
                "      \"chest_location_x\": 1.47000002861023,\n" +
                "      \"chest_location_z\": -21.2000007629395,\n" +
                "      \"chest_num\": 2,\n" +
                "      \"create_time\": 1693037962,\n" +
                "      \"custom_room_id\": 1082289,\n" +
                "      \"gmod\": 17,\n" +
                "      \"is_get\": 0,\n" +
                "      \"mmod\": 41,\n" +
                "      \"pid\": 1247470463,\n" +
                "      \"round\": 3,\n" +
                "      \"show_gmod\": 5,\n" +
                "      \"time\": 1698835804,\n" +
                "      \"tmod\": 1,\n" +
                "      \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"create_time\": 1693037962,\n" +
                "  \"custom_room_id\": 1082289,\n" +
                "  \"game_area\": \"CN\",\n" +
                "  \"gmod\": 17,\n" +
                "  \"mmod\": 41,\n" +
                "  \"pid\": 1247470463,\n" +
                "  \"platform_id\": 0,\n" +
                "  \"round\": 3,\n" +
                "  \"show_gmod\": 5,\n" +
                "  \"time\": 1698835804,\n" +
                "  \"tmod\": 1,\n" +
                "  \"unet_area\": \"TX_NJ\",\n" +
                "  \"war_id\": \"3b4b672fadf4b0e987fd4306388dad83_1\",\n" +
                "  \"#data_lifecycle\": \"0\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode topNodes = mapper.readValue(content, ObjectNode.class);

        String path1 = "/chest_list/[0]/chest_location_x";
        String path2 = "/chest_list/[0]/chest_location_z";
        String path3 = "/chest_list/[1]/chest_location_x";
        String path4 = "/chest_list/[1]/chest_location_z";


        deleteElementByPath(topNodes, path1);
        deleteElementByPath(topNodes, path2);
        deleteElementByPath(topNodes, path3);
        deleteElementByPath(topNodes, path4);

        String result2 = mapper.writeValueAsString(topNodes);
        System.out.println(result2);

    }

    public static void deleteElementByPath(ObjectNode topNodes, String removePath) {
        String removeElementIndex = removePath.substring(removePath.lastIndexOf('/') + 1);
        String parentPath = removePath.substring(0, removePath.lastIndexOf('/'));
        parentPath = parentPath.replaceAll("\\[", "");
        parentPath = parentPath.replaceAll("]", "");
        System.out.println(parentPath);

        JsonPointer removeElementParentJsonPointer = JsonPointer.compile(parentPath);
        if (StringUtils.startsWith(removeElementIndex, "[")) {
            ArrayNode parent = (ArrayNode) topNodes.at(removeElementParentJsonPointer);
            String arrayIndex = removeElementIndex.substring(1, removeElementIndex.length() - 1);
            parent.remove(Integer.parseInt(arrayIndex));
        } else {
            JsonNode parentNode = topNodes.at(removeElementParentJsonPointer);
            if (!parentNode.isMissingNode()) {
                ObjectNode parent = (ObjectNode) parentNode;
                parent.remove(removeElementIndex);
            }
        }
    }


}


