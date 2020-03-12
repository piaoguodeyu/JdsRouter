package com.hjds.hjdsrouterprocessor.router;


import com.google.auto.service.AutoService;
import com.hjds.jrouterannotation.JRouterProvider;
import com.hjds.jrouterannotation.Router;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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


@AutoService(Processor.class)
@SupportedOptions("moduleName")
public class RouteAnnoProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //拿到模块的build.gradle里 arguments = [moduleName : project.getName()]
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

//        //1. 拿到所有注解
//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(XLRouteAnno.class);
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
        ClassName provider = ClassName.get(JRouterProvider.class);
        TypeSpec.Builder mainActivityBuilder = TypeSpec.classBuilder("JRouterProviderImp")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(provider);

        MethodSpec constructorBuilder = MethodSpec.constructorBuilder()
                .addStatement("        initData()")
                .addModifiers(Modifier.PUBLIC).build();


        mainActivityBuilder.addMethod(constructorBuilder);
        ClassName hashmap = ClassName.get(HashMap.class);
        FieldSpec filedspec = FieldSpec.builder(hashmap, "mMap")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T()", hashmap).build();

        mainActivityBuilder.addField(filedspec);


        MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("initData")
//                .addAnnotation(override)
                .addModifiers(Modifier.PROTECTED, Modifier.STATIC);

        methodBuild.addCode("if (mMap.isEmpty()) {\n")
                .addCode("synchronized (JRouterProviderImp.class) {\n");
        methodBuild.addCode("if (mMap.isEmpty()) {\n");

        for (TypeElement element : set) {
            Router routeAnno = element.getAnnotation(Router.class);
//            "mMap.put($S,$T.class)"
            methodBuild.addStatement("mMap.put($S,$T.class)",
                    routeAnno.path(), ClassName.get(element));
        }
        methodBuild.addCode("}");
        methodBuild.addCode("}");
        methodBuild.addCode("}");
        MethodSpec onCreate = methodBuild
//                .addParameter(savedInstanceState)
                .build();


        MethodSpec impmethod = MethodSpec.methodBuilder("getAllRouter")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return mMap")
                .returns(hashmap).build();
        mainActivityBuilder.addMethod(impmethod);

        TypeSpec mainActivity = mainActivityBuilder.addMethod(onCreate)
                .build();

        JavaFile file = JavaFile.builder("com.hjds.hjdsrouterlib", mainActivity).build();

        try {
            file.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            e.printStackTrace();
        }


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
