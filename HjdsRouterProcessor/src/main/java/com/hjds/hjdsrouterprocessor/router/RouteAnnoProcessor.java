package com.hjds.hjdsrouterprocessor.router;


import com.google.auto.service.AutoService;
import com.hjds.hjdsrouterprocessor.AnnoLogger;
import com.hjds.jrouterannotation.JRouter;
import com.hjds.jrouterannotation.JRouterProvider;
import com.hjds.jrouterannotation.XLRouteAnno;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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


@AutoService(Processor.class)
@SupportedOptions("moduleName")
public class RouteAnnoProcessor extends AbstractProcessor {
    public static final String OPTION_MODULE_NAME = "moduleName";

    private String mModuleName;
    private AnnoLogger mLogger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //拿到模块的build.gradle里 arguments = [moduleName : project.getName()]
        mModuleName = processingEnvironment.getOptions().get(OPTION_MODULE_NAME);
        mLogger = new AnnoLogger(processingEnvironment.getMessager());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (mModuleName == null || "".equals(mModuleName)) {
            throw new RuntimeException("mModuleName is Empty, check build.gradle");
        }

//        //1. 拿到所有注解
//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(XLRouteAnno.class);
        //1. 拿到所有注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(JRouter.class);

        //2. 如果注解为空 ，则结束
        if (elements == null || elements.size() == 0) {
            return true;
        }
//        for (Element element : elements) {
//            System.out.println("elementelementelement:" + element.toString());
//            mLogger.error("elementelementelement:" + element.toString());
//
//        }
        //3.校验注解
        Set<TypeElement> checkedTypeElement = new HashSet<>();
        for (Element element : elements) {
            if (!(element instanceof TypeElement)) continue;
            if (!element.getKind().isClass()) continue;
//            if (!validateElement((TypeElement) element)) continue;
            checkedTypeElement.add((TypeElement) element);
        }
        String name = XLRouteAnnoHelper.reFormatModuleName(mModuleName);
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

//        ClassName override = ClassName.get("java.lang", "Override");
//
//        ClassName bundle = ClassName.get("android.os", "Bundle");
//
//        ClassName nullable = ClassName.get("android.support.annotation", "Nullable");

//        ParameterSpec savedInstanceState = ParameterSpec.builder(bundle, "savedInstanceState")
//                .addAnnotation(nullable)
//                .build();
//        HashMap map=new HashMap();
//        map.put("",ParameterizedTypeName.class);
//        ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class)
//                , ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class)));
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
            JRouter routeAnno = element.getAnnotation(JRouter.class);
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

    private boolean validateElement(TypeElement element) {
        boolean isSubOfActivity = processingEnv.getTypeUtils().isSubtype(element.asType(), processingEnv.getElementUtils().getTypeElement("android.app.Activity").asType());

        if (!isSubOfActivity) return false;

        Set<Modifier> modifiers = element.getModifiers();

        return modifiers == null || !modifiers.contains(Modifier.ABSTRACT);
    }

    /**
     * 生成java文件
     *
     * @param moduleName
     * @param elements
     */
    private void saveToJava(String moduleName, Set<TypeElement> elements) {
        //Map<String, Class<?>>;
        ParameterizedTypeName parameter = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class)
                , ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class)));

        ParameterSpec parameterSpec = ParameterSpec.builder(parameter, "map").build();

        //@override
        //public void handleRoute(Map<String, Class<?>> map)
        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("handleRoute")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);

        Set<String> pathSet = new HashSet<>();

        //遍历
        for (TypeElement element : elements) {
            XLRouteAnno routeAnno = element.getAnnotation(XLRouteAnno.class);
            String path = routeAnno.value();
            mLogger.info("kimmyTest:" + routeAnno.prefix() + " path= " + path);

            //检查空路径、检查重复路径
            if ("".equals(path)) {
                throw new RuntimeException(String.format("empty path in module %s;  in Activity : %s", mModuleName, element.getQualifiedName()));
            }

            if (pathSet.contains(path)) {
                throw new RuntimeException(String.format("%s path duplicated in module %s; Activity %s", path, mModuleName, element.getQualifiedName()));
            }

            methodSpecBuilder.addStatement("map.put($S,$T.class)", path, ClassName.get(element));
            pathSet.add(path);
        }

        //方法块结束，开始创建类
        TypeElement superType = processingEnv.getElementUtils().getTypeElement(IRouteContentProvider.class.getName());
        // AppRouteContentProvider
        TypeSpec typeSpec = TypeSpec.classBuilder(moduleName + XLRouteAnnoHelper.GENERATE_ROUTE_SUFFIX_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(superType))
                .addMethod(methodSpecBuilder.build())
                .addJavadoc(XLRouteAnnoHelper.CLASS_JAVA_DOC).build();

        try {
            JavaFile.builder(XLRouteAnnoHelper.ROUTE_PACKAGE_NAME, typeSpec)
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //支持的注解类型
        return Collections.singleton(JRouter.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //
        return SourceVersion.latestSupported();
    }

}
