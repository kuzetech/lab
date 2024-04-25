package com.kuze.bigdata.rengine.benchmark.complex;

import com.fasterxml.jackson.core.JsonProcessingException;
import groovy.lang.GroovyClassLoader;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

import java.lang.reflect.InvocationTargetException;

public class BenchmarkComplex {

    private static void benchmarkForJava() throws JsonProcessingException {
        long start2 = System.currentTimeMillis();

        Processable processable = new NativeProcessable();

        for (int i = 0; i < 1000000; i++) {
            if (processable.filter(i)) {
                processable.process(i);
            }
        }

        long end2 = System.currentTimeMillis();

        System.out.println("java:" + (end2 - start2) + " ms");
    }

    public static void benchmarkForGroovyClassLoader() {

        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

        String groovyCode = "import com.fasterxml.jackson.core.JsonProcessingException;\n" +
                "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                "import com.fasterxml.jackson.databind.node.ObjectNode;\n" +
                "\n" +
                "import java.nio.charset.StandardCharsets;\n" +
                "import java.security.MessageDigest;\n" +
                "import java.security.NoSuchAlgorithmException;\n" +
                "import java.util.UUID;\n" +
                "\n" +
                "public class GroovyProcessable implements com.kuze.bigdata.rengine.benchmark.complex.Processable {\n" +
                "\n" +
                "    private static final ObjectMapper mapper = new ObjectMapper();\n" +
                "    private static final MessageDigest md;\n" +
                "\n" +
                "    static {\n" +
                "        try {\n" +
                "            md = MessageDigest.getInstance(\"MD5\");\n" +
                "        } catch (NoSuchAlgorithmException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    ;\n" +
                "\n" +
                "    @Override\n" +
                "    public Boolean filter(int i) {\n" +
                "        return (i % 2 == 0);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void process(int i) throws JsonProcessingException {\n" +
                "        com.kuze.bigdata.rengine.benchmark.complex.Event e = new com.kuze.bigdata.rengine.benchmark.complex.Event();\n" +
                "        e.setId(UUID.randomUUID().toString());\n" +
                "        e.setName(\"Login\");\n" +
                "        e.setTime(System.currentTimeMillis());\n" +
                "        e.setIp(com.kuze.bigdata.rengine.benchmark.complex.Event.generateRandomIPAddress());\n" +
                "\n" +
                "        ObjectNode objectNode = (ObjectNode) mapper.valueToTree(e);\n" +
                "        long milSecond = objectNode.get(\"time\").asLong();\n" +
                "        objectNode.put(\"second_time\", milSecond / 1000);\n" +
                "\n" +
                "        byte[] hash = md.digest(e.getIp().getBytes(StandardCharsets.UTF_8));\n" +
                "        // 将 byte 数组转换为十六进制字符串\n" +
                "        StringBuilder hexString = new StringBuilder();\n" +
                "        for (byte b : hash) {\n" +
                "            String hex = Integer.toHexString(0xff & b);\n" +
                "            if (hex.length() == 1) hexString.append('0');\n" +
                "            hexString.append(hex);\n" +
                "        }\n" +
                "        objectNode.put(\"id_hash\", hexString.toString());\n" +
                "\n" +
                "        mapper.writeValueAsString(objectNode);\n" +
                "    }\n" +
                "}";
        try {
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyCode);
            Processable processable = (Processable) groovyClass.newInstance();

            long start1 = System.currentTimeMillis();

            for (int i = 0; i < 1000000; i++) {
                if (processable.filter(i)) {
                    processable.process(i);
                }
            }

            long end1 = System.currentTimeMillis();

            System.out.println("groovy:" + (end1 - start1) + " ms");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void benchmarkForJanino() throws CompileException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, JsonProcessingException {

        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(BenchmarkComplex.class.getClassLoader());

        String content = "import com.fasterxml.jackson.core.JsonProcessingException;\n" +
                "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                "import com.fasterxml.jackson.databind.node.ObjectNode;\n" +
                "\n" +
                "import java.nio.charset.StandardCharsets;\n" +
                "import java.security.MessageDigest;\n" +
                "import java.security.NoSuchAlgorithmException;\n" +
                "import java.util.UUID;\n" +
                "\n" +
                "public class JaninoProcessable implements com.kuze.bigdata.rengine.benchmark.complex.Processable {\n" +
                "\n" +
                "    private static final ObjectMapper mapper = new ObjectMapper();\n" +
                "    private static final MessageDigest md;\n" +
                "\n" +
                "    static {\n" +
                "        try {\n" +
                "            md = MessageDigest.getInstance(\"MD5\");\n" +
                "        } catch (NoSuchAlgorithmException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    ;\n" +
                "\n" +
                "    @Override\n" +
                "    public Boolean filter(int i) {\n" +
                "        return (i % 2 == 0);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void process(int i) throws JsonProcessingException {\n" +
                "        com.kuze.bigdata.rengine.benchmark.complex.Event e = new com.kuze.bigdata.rengine.benchmark.complex.Event();\n" +
                "        e.setId(UUID.randomUUID().toString());\n" +
                "        e.setName(\"Login\");\n" +
                "        e.setTime(System.currentTimeMillis());\n" +
                "        e.setIp(com.kuze.bigdata.rengine.benchmark.complex.Event.generateRandomIPAddress());\n" +
                "\n" +
                "        ObjectNode objectNode = (ObjectNode) mapper.valueToTree(e);\n" +
                "        long milSecond = objectNode.get(\"time\").asLong();\n" +
                "        objectNode.put(\"second_time\", milSecond / 1000);\n" +
                "\n" +
                "        byte[] hash = md.digest(e.getIp().getBytes(StandardCharsets.UTF_8));\n" +
                "        // 将 byte 数组转换为十六进制字符串\n" +
                "        StringBuilder hexString = new StringBuilder();\n" +
                "        for (byte b : hash) {\n" +
                "            String hex = Integer.toHexString(0xff & b);\n" +
                "            if (hex.length() == 1) hexString.append('0');\n" +
                "            hexString.append(hex);\n" +
                "        }\n" +
                "        objectNode.put(\"id_hash\", hexString.toString());\n" +
                "\n" +
                "        mapper.writeValueAsString(objectNode);\n" +
                "    }\n" +
                "}";
        compiler.cook(content);

        Class<?> aClass = compiler.getClassLoader().loadClass("JaninoProcessable");

        Processable processable = (Processable) aClass.newInstance();

        long start2 = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            if (processable.filter(i)) {
                processable.process(i);
            }
        }

        long end2 = System.currentTimeMillis();

        System.out.println("janino:" + (end2 - start2) + " ms");
    }

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 5; i++) {
            benchmarkForJava();

            // janino
            benchmarkForJanino();

            // groovy classloader
            benchmarkForGroovyClassLoader();

            System.out.println();
        }
    }

}
