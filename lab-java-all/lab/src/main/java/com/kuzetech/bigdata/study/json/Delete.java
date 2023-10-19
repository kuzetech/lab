package com.kuzetech.bigdata.study.json;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

public class Delete {

    public static void main(String[] args) throws JsonProcessingException {
        String content = "{\n" +
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

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode topNodes = mapper.readValue(content, ObjectNode.class);

        String result1 = mapper.writeValueAsString(topNodes);
        System.out.println(result1);

        String path1 = "/glossary/GlossDiv/GlossList/GlossEntry/ID";
        String path2 = "/glossary/GlossDiv/GlossList/GlossEntry/GlossDef/GlossSeeAlso/[1]";

        deleteElementByPath(topNodes, path1);
        deleteElementByPath(topNodes, path2);

        String result2 = mapper.writeValueAsString(topNodes);
        System.out.println(result2);

    }

    public static void deleteElementByPath(ObjectNode topNodes, String removePath) {
        String removeElementIndex = removePath.substring(removePath.lastIndexOf('/')+1);
        JsonPointer removeElementParentJsonPointer = JsonPointer.compile(removePath.substring(0, removePath.lastIndexOf('/')));
        if (StringUtils.startsWith(removeElementIndex, "[")){
            ArrayNode parent = (ArrayNode)topNodes.at(removeElementParentJsonPointer);
            String arrayIndex = removeElementIndex.substring(1, removeElementIndex.length() - 1);
            parent.remove(Integer.parseInt(arrayIndex));
        }else{
            ObjectNode parent = (ObjectNode)topNodes.at(removeElementParentJsonPointer);
            parent.remove(removeElementIndex);
        }
    }
}


