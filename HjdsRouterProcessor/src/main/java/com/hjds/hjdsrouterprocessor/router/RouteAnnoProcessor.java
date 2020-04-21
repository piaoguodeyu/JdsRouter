package com.hjds.hjdsrouterprocessor.router;


import com.google.auto.service.AutoService;
import com.hjds.jrouterannotation.Router;
import com.hjds.jrouterannotation.RouterProvider;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedOptions("moduleName")
public class RouteAnnoProcessor extends AbstractProcessor {
    String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //拿到模块的build.gradle里 arguments = [moduleName : project.getName()]
        Map<String, String> options = processingEnv.getOptions();
        if (options == null || options.isEmpty()) {
            return;
        }
        moduleName = options.get("jdsModuleName");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (moduleName == null || moduleName.equals("")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "请在defaultConfig里配置\n" +
                    "javaCompileOptions {\n" +
                    "            annotationProcessorOptions {\n" +
                    "                arguments = [jdsModuleName: project.getName()]\n" +
                    "            }\n" +
                    "        }");
            return true;
        }
        //1. 拿到所有注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Router.class);

        //2. 如果注解为空 ，则结束
        if (elements == null || elements.size() == 0) {
            return true;
        }
        //3.校验注解
        Set<TypeElement> checkedTypeElement = new HashSet<>();
        for (Element element : elements) {
            if (!(element instanceof TypeElement)) continue;
            if (!element.getKind().isClass()) continue;
//            if (!validateElement((TypeElement) element)) continue;
            checkedTypeElement.add((TypeElement) element);
        }
//        //写入本地java类
//        saveToJava(name, checkedTypeElement);
        saveToJava(checkedTypeElement);
        return true;
    }

    public void saveToJava(Set<TypeElement> set) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "RouteAnnoProcessorRouteAnno+moduleName=- " + moduleName);
        ClassName provider = ClassName.get(RouterProvider.class);


        String clazzName = "RouterProviderImp" + "$" + moduleName;
        TypeSpec.Builder mainActivityBuilder = TypeSpec.classBuilder(clazzName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(provider);

//        MethodSpec constructorBuilder = MethodSpec.constructorBuilder()
//                .addStatement("init()")
//                .addModifiers(Modifier.PUBLIC).build();
//        mainActivityBuilder.addMethod(constructorBuilder);


//        ClassName hashmap = ClassName.get(HashMap.class);
//        FieldSpec filedspec = FieldSpec.builder(hashmap, "mMap")
//                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
//                .initializer("new $T()", hashmap).build();

//        mainActivityBuilder.addField(filedspec);


        MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("init")
//                .addAnnotation(override)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        mainActivityBuilder.addMethod(methodBuild.build());
//        methodBuild.addCode("if (mMap.isEmpty()) {\n")
//                .addCode("synchronized (RouterProvider.class) {\n");
//        methodBuild.addCode("if (mMap.isEmpty()) {\n");

        CodeBlock.Builder codeBlock = CodeBlock.builder();
        for (TypeElement element : set) {
            Router routeAnno = element.getAnnotation(Router.class);
//            "mMap.put($S,$T.class)"
//            methodBuild.addStatement("mMap.put($S,$T.class)",
//                    routeAnno.path(), ClassName.get(element));
            codeBlock.addStatement("mMap.put($S,$T.class)",
                    routeAnno.path(), ClassName.get(element));
        }
//        methodBuild.addCode("}");
//        methodBuild.addCode("}");
//        methodBuild.addCode("}");
//        MethodSpec onCreate = methodBuild
//                .build();


//        MethodSpec impmethod = MethodSpec.methodBuilder("getAllRouter")
//                .addAnnotation(Override.class)
//                .addModifiers(Modifier.PUBLIC)
//                .addStatement("return mMap")
//                .returns(hashmap).build();
//        mainActivityBuilder.addMethod(impmethod);


        TypeSpec mainActivity = mainActivityBuilder
//                .addMethod(onCreate)
                .addStaticBlock(codeBlock.build())
                .build();

        JavaFile file = JavaFile.builder("com.hjds.routerlibs", mainActivity).build();
        log("88888888888888888888888888");
        try {
            file.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //支持的注解类型
        return Collections.singleton(Router.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //
        return SourceVersion.latestSupported();
    }
}
