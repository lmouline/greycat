/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.modeling.generator;

import com.intellij.openapi.util.io.FileUtil;
import greycat.modeling.language.TypedGraph;
import greycat.modeling.language.TypedGraphBuilder;
import greycat.modeling.language.impl.TypedGraphImpl;
import java2typescript.SourceTranslator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.jboss.forge.roaster.model.source.JavaSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class APIGenerator {
    public static final String FILE_EXTENSION = ".gcm";

    private final TypedGraph graph;

    public APIGenerator() {
        this.graph = new TypedGraphImpl();
    }

    public void scan(File target) throws Exception {
        if(target.isDirectory()) {
            String[] everythingInThisDir = target.list();
            if(everythingInThisDir == null) {
                //todo error
            } else {
                for (String name : everythingInThisDir) {
                    if (name.trim().endsWith(FILE_EXTENSION)) {
                        TypedGraphBuilder.parse(new File(target, name), graph);
                    }
                }
            }

        } else if(target.getName().endsWith(FILE_EXTENSION)) {
            TypedGraphBuilder.parse(target, graph);
        } else {
            //todo error
        }
    }

    public void deepScan(File target) throws Exception {
        if(target.isDirectory()) {
            String[] everythingInThisDir = target.list();
            if(everythingInThisDir == null) {
                //todo error
            } else {
                for (String name : everythingInThisDir) {
                    if (name.trim().endsWith(FILE_EXTENSION)) {
                        TypedGraphBuilder.parse(new File(target, name), graph);
                    } else {
                        File current = new File(target, name);
                        if (current.isDirectory()) {
                            deepScan(current);
                        }
                    }
                }
            }

        } else if(target.getName().endsWith(FILE_EXTENSION)) {
            TypedGraphBuilder.parse(target,graph);
        }
    }

    private void generateJava(String packageName, String pluginName, File target) {
        int index = 0;
        JavaSource[] sources = new JavaSource[(graph.classifiers().length) * 2 + 2];
        sources[index] = PluginClassGenerator.generate(packageName, pluginName, graph);
        index++;

        JavaSource[] nodeTypes = NodeTypeGenerator.generate(packageName, pluginName, graph);
        System.arraycopy(nodeTypes, 0, sources, index, nodeTypes.length);
        index += nodeTypes.length;

        JavaSource[] tasksApi = TaskApiGenerator.generate(packageName, graph);
        System.arraycopy(tasksApi, 0, sources, index, tasksApi.length);
        index += tasksApi.length;

        for (int i = 0; i < index; i++) {
            if (sources[i] != null) {
                JavaSource src = sources[i];
                File targetPkg;
                if (src.getPackage() != null) {
                    targetPkg = new File(target.getAbsolutePath() + File.separator + src.getPackage().replace(".", File.separator));
                } else {
                    targetPkg = target;
                }
                targetPkg.mkdirs();
                File targetSrc = new File(targetPkg, src.getName() + ".java");
                try {
                    FileWriter writer = new FileWriter(targetSrc);
                    writer.write(src.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateJS(String packageName, String pluginName, File target, String gcVersion, MavenProject mvnProject) {//todo move to generator
        // Generate TS
        SourceTranslator transpiler = new SourceTranslator(Arrays.asList(target.getAbsolutePath()), target.getAbsolutePath() + "ts", packageName);

        if(mvnProject != null) {
            for (Artifact a : mvnProject.getArtifacts()) {
                File file = a.getFile();
                if (file != null) {
                    if (file.isFile()) {
                        transpiler.addToClasspath(file.getAbsolutePath());
                    }
                }
            }
        } else {
            addToTransClassPath(transpiler);
        }

        transpiler.process();
        transpiler.addHeader("import * as greycat from 'greycat'");
        transpiler.generate();


        File tsGen = new File(target.getAbsolutePath() + "ts/" + packageName + ".ts");
        try {
            FileUtil.writeToFile(tsGen,("export = " + packageName).getBytes(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String tsConfigContent = "{\n" +
                "  \"compilerOptions\": {\n" +
                "    \"module\": \"commonjs\",\n" +
                "    \"noImplicitAny\": false,\n" +
                "    \"removeComments\": true,\n" +
                "    \"preserveConstEnums\": true,\n" +
                "    \"sourceMap\": true,\n" +
                "    \"target\": \"es5\",\n" +
                "    \"declaration\": true,\n" +
                "    \"outDir\": \"../" +target.getName() + "js/" + packageName + "\"\n" +
                "  },\n" +
                "  \"files\": [\n" +
                "    \"" + packageName + ".ts\"\n" +
                "  ]\n" +
                "}";


        try {
            File tsConfig = new File(target.getAbsolutePath() + "ts/tsconfig.json");
            tsConfig.createNewFile();
            FileUtil.writeToFile(tsConfig,tsConfigContent);
        } catch (IOException e) {
            e.printStackTrace();
        }


        boolean isSnaphot = (gcVersion.contains("SNAPSHOT"));
        gcVersion = isSnaphot?  "./greycat" : "^" + gcVersion + ".0.0";

        String packageJsonContent = "{\n" +
                "  \"name\": \"" + packageName + "\",\n" +
                "  \"version\": \"1.0.0\",\n" +
                "  \"description\": \"\",\n" +
                "  \"main\": \"main.js\",\n" +
                "  \"author\": \"\",\n" +
                "  \"dependencies\": {\n" +
                "    \"greycat\": \"" +  gcVersion+ "\"\n" +
                "  },\n" +
                "  \"devDependencies\": {\n" +
                "    \"typescript\": \"^2.1.5\"\n" +
                "  }" +
                "}";
        try {
            File packageJson = new File(target.getAbsolutePath() + "ts/package.json");
            packageJson.createNewFile();
            FileUtil.writeToFile(packageJson,packageJsonContent);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Generate a base of NPM project
        File npmProject = new File(target.getAbsolutePath() + "js");
        npmProject.mkdirs();
        File mainJS = new File(npmProject,"main.js");
        File packageJson2 = new File(npmProject,"package.json");

        File npmModule = new File(npmProject.getAbsolutePath() + "/" + packageName);
        npmModule.mkdirs();
        File packageJson3 = new File(npmModule,"package.json");

        try {
            mainJS.createNewFile();
            FileUtil.writeToFile(mainJS,"var greycat = require(\"greycat\");\n" +
                    "var " + packageName + " = require(\"" + packageName + "\");\n" +
                    "\n" +
                    "var builder = new greycat.GraphBuilder().withPlugin(new " + packageName + "." + pluginName + "());\n" +
                    "var graph = builder.build();\n" +
                    "\n" +
                    "graph.connect(function (isSucceed) {\n" +
                    "\n" +
                    "});");

            packageJson2.createNewFile();
            FileUtil.writeToFile(packageJson2,"{\n" +
                    "  \"name\": \"" + packageName + "\",\n" +
                    "  \"version\": \"1.0.0\",\n" +
                    "  \"description\": \"\",\n" +
                    "  \"main\": \"main.js\",\n" +
                    "  \"author\": \"\",\n" +
                    "  \"dependencies\": {\n" +
                    "    \"greycat\": \"" + gcVersion + "\",\n" +
                    "    \"smarthome\": \"./smarthome\"\n" +
                    "  }\n" +
                    "}");

            packageJson3.createNewFile();
            FileUtil.writeToFile(packageJson3,"{\n" +
                    "  \"name\": \"smarthome\",\n" +
                    "  \"description\": \"TypedGraph\",\n" +
                    "  \"version\": \"1.0.0\",\n" +
                    "  \"main\": \"smarthome.js\",\n" +
                    "  \"typings\": \"smarthome.d.ts\",\n" +
                    "  \"dependencies\": {\n" +
                    "    \"greycat\": \"" + gcVersion +"\"\n" +
                    "  }\n" +
                    "}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!isSnaphot) {
            File workingDFir = new File(target.getAbsolutePath() + "ts");

            // Install required package in TS
            ProcessBuilder processBuilder = new ProcessBuilder("npm","install");
            processBuilder.directory(workingDFir);
            processBuilder.inheritIO();

            // Run TSC
            ProcessBuilder processBuilder2 = new ProcessBuilder("node","node_modules/typescript/lib/tsc.js");
            processBuilder2.directory(workingDFir);
            processBuilder2.inheritIO();

            //Intsall required packaged in JS project
            ProcessBuilder processBuilder3 = new ProcessBuilder("npm","install");
            processBuilder3.directory(npmProject);
            processBuilder3.inheritIO();


            try {
                processBuilder.start().waitFor();
                processBuilder2.start().waitFor();
                processBuilder3.start().waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Do not forget to copy past the greycat.js file and update the path in package.json if needed");
        }
    }

    public void mvnGenerate(String packageName, String pluginName, File target, boolean generateJava, boolean generateJS, String gcVersion, MavenProject project) {
        if(generateJava || generateJS) {
            generateJava(packageName,pluginName,target);
        }

        if(generateJS) {
            generateJS(packageName,pluginName,target,gcVersion, project);
        }
    }


    public void generate(String packageName, String pluginName, File target, boolean generateJava, boolean generateJS, String gcVersion) {
        if(generateJava || generateJS) {
            generateJava(packageName,pluginName,target);
        }

        if(generateJS) {
            generateJS(packageName,pluginName,target,gcVersion,null);
        }
    }


    private void addToTransClassPath(SourceTranslator transpiler) {
        String classPath = System.getProperty("java.class.path");
        int index=0;
        boolean finish = false;
        while(index < classPath.length() && !finish) {
            if(classPath.charAt(index) == ':') {
                int slashIdx = index;
                while(slashIdx >= 0 && !finish) {
                    if(classPath.charAt(slashIdx) == '/') {
                        if(slashIdx+7 < index && classPath.charAt(slashIdx + 1) == 'g' && classPath.charAt(slashIdx + 2) == 'r' && classPath.charAt(slashIdx + 3) == 'e'
                                && classPath.charAt(slashIdx + 4) == 'y' && classPath.charAt(slashIdx + 5) == 'c' && classPath.charAt(slashIdx + 6) == 'a' &&  classPath.charAt(slashIdx + 7) == 't') {
                            while(slashIdx >= 0 && !finish) {
                                if(classPath.charAt(slashIdx) == ':') {
                                    transpiler.addToClasspath(classPath.substring(slashIdx + 1,index));
                                    finish = true;
                                }
                                slashIdx--;
                            }
                        }
                    }
                    slashIdx--;
                }
            }
            index++;
        }
    }


}
