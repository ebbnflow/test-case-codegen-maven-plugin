package com.github.ebbnflow;


import com.google.common.base.Strings;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnitTestGenerator<T> {
    Token rootToken;
    Token importsTokenRoot;

    public void generateTest(T obj) throws IOException {
        rootToken = new Token();
        rootToken.setTokenStart("package " + obj.getClass().getPackage().getName() + ";");
        importsTokenRoot = rootToken.createChildToken();

        Token classNameToken = rootToken.createChildToken();
        //public void myTestMethod(){
        Token unitTestMethodNameRootToken = writeUnitTestName(obj.getClass().getSimpleName(), classNameToken);
        // MyType type = createMyType(); // for every complex object under this root, there will be a createType() factory method
        writeFactoryCreationMethod(obj, unitTestMethodNameRootToken);


        //assertEquals(type.field1, "value of field 1", "field name should be equal")
        //assertMyNestedObj(type.getMyNestedObj()); //for every nested obj, make an assertion method that will assert all items in that object.
        //}
        printCode();
    }

    private void writeFactoryCreationMethod(T obj, Token token) {
        token.createChildToken(obj.getClass().getName() + " a" + obj.getClass().getSimpleName() + " = create" + obj.getClass().getSimpleName() + "();", null);
        importsTokenRoot.createChildToken("import " + obj.getClass().getName() + ";", null);
    }

    private List<Field> createFieldList(Class<T> clazz) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        }
        return fields;
    }

    private Token writeUnitTestName(String name, Token token) {
        token.setTokenStart("public class " + name + "Test {");
        Token currentToken = token.createChildToken("public void " + name + "Validation() {", "}");
        token.setTokenEnd("}");
        return currentToken;
    }

    private void printCode() throws IOException {
        StringBuilder builder = new StringBuilder();
        FileWriter fstream = new FileWriter("./src/main/java/GeneratedCode.java");
        BufferedWriter info = new BufferedWriter(fstream);
        writeToken(info, rootToken);
        info.close();
    }

    private void writeToken(BufferedWriter info, Token token) throws IOException {
        if (!Strings.isNullOrEmpty(token.getTokenStart())) {
            info.write(token.getTokenStart());
            info.newLine();
        }
        if (null != token.getChildTokens() && token.getChildTokens().size() > 0) {
            for (Token nestedToken : token.getChildTokens()) {
                writeToken(info, nestedToken);
            }
        }

        if (Strings.isNullOrEmpty(token.getTokenEnd()))
            return;

        info.write(token.getTokenEnd());
        info.newLine();
    }
}
