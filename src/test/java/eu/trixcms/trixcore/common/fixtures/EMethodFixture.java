package eu.trixcms.trixcore.common.fixtures;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.response.IResponse;
import eu.trixcms.trixcore.common.MethodManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public enum EMethodFixture {

    HELLO("hello", EJsonResponseFixture.OK),
    BYE("bye", EJsonResponseFixture.NOT_FOUND),

    ;

    private String name;
    private EJsonResponseFixture response;

    EMethodFixture(String name, EJsonResponseFixture response) {
        this.name = name;
        this.response = response;
    }

    public IMethod create() {
        return create(response);
    }

    public IMethod create(EJsonResponseFixture response) {
        return create(response.create());
    }

    public IMethod create(IResponse response) {
        switch (name){
            case "hello":
                return new HelloMethod(response);
            case "bye":
                return new ByeMethod(response);
        }

        return null;
    }


    @RequiredArgsConstructor
    @MethodName(name = "hello")
    private static final class HelloMethod implements IMethod {

        private final IResponse response;

        @Override
        public IResponse exec(String[] args) {
            return response;
        }
    }

    @RequiredArgsConstructor
    @MethodName(name = "bye")
    private static final class ByeMethod implements IMethod {

        private final IResponse response;

        @Override
        public IResponse exec(String[] args) {
            return response;
        }
    }

}
