package org.kmf.generator;

import java.io.File;

public class GeneratorTest {

    //@Test
    public void test() throws Exception {
        Generator gen = new Generator();
        String resourcesPath = GeneratorTest.class.getClassLoader().getResource(".").getFile();
        gen.scan(new File(resourcesPath));
        gen.generate("org.mwg.generator.test.TestModel", new File(resourcesPath + "/../generated-sources"));
    }

}
