package eu.trixcms.trixcore.common;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.method.exception.ArgsPreconditionFailedException;
import eu.trixcms.trixcore.api.method.exception.DuplicateMethodNameException;
import eu.trixcms.trixcore.api.method.exception.InvalidMethodDefinitionException;
import eu.trixcms.trixcore.api.method.exception.MethodNotFoundException;
import eu.trixcms.trixcore.api.response.IResponse;
import eu.trixcms.trixcore.common.fixtures.EJsonResponseFixture;
import eu.trixcms.trixcore.common.fixtures.EMethodFixture;
import eu.trixcms.trixcore.common.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class MethodManagerTest {

    private String getMethodName(IMethod method){
        try {
            MethodName methodName = method.getClass().getAnnotation(MethodName.class);
            if (!methodName.name().equals("none")) {
                return methodName.name();
            } else if (!methodName.method().equals(Methods.NONE)) {
                return methodName.method().getSlug();
            }

            return null;
        } catch(Exception e){
            return null;
        }
    }

    @Test
    public void addMethod() throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        MethodManager methodManager = new MethodManager();
        IMethod method = EMethodFixture.HELLO.create();
        methodManager.addMethod(method);

        Map<String, IMethod> retrieved = methodManager.getMethods();

        assertThat(retrieved).hasSize(1);
        assertThat(retrieved.keySet()).containsExactly(getMethodName(method));
        assertThat(retrieved.values()).containsExactly(method);
    }

    @Test
    public void addMethods() throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        MethodManager methodManager = new MethodManager();
        IMethod method1 = EMethodFixture.HELLO.create();
        IMethod method2 = EMethodFixture.BYE.create();

        methodManager.addMethods(Arrays.asList(method1, method2));

        Map<String, IMethod> retrieved = methodManager.getMethods();

        assertThat(retrieved).hasSize(2);
        assertThat(retrieved.keySet()).containsExactly(getMethodName(method1), getMethodName(method2));
        assertThat(retrieved.values()).containsExactly(method1, method2);
    }

    @Test
    public void removeMethodByObject() throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        MethodManager methodManager = new MethodManager();
        IMethod method = EMethodFixture.HELLO.create();
        methodManager.addMethod(method);
        methodManager.removeMethod(method);

        assertThat(methodManager.getMethods()).hasSize(0);
    }

    @Test
    public void removeMethodByName() throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        MethodManager methodManager = new MethodManager();
        IMethod method = EMethodFixture.HELLO.create();
        methodManager.addMethod(method);
        methodManager.removeMethod(getMethodName(method));

        assertThat(methodManager.getMethods()).hasSize(0);
    }

    @Test(expected = DuplicateMethodNameException.class)
    public void duplicateMethod() throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        MethodManager methodManager = new MethodManager();
        methodManager.addMethod(EMethodFixture.HELLO.create());
        methodManager.addMethod(EMethodFixture.HELLO.create());
    }

    @Test(expected = MethodNotFoundException.class)
    public void dispatchWithNotFoundMethod() throws MethodNotFoundException, ArgsPreconditionFailedException {
        MethodManager methodManager = new MethodManager();
        methodManager.dispatch("method not found", new String[]{});
    }

    @Test
    public void dispatchMethod() throws DuplicateMethodNameException, MethodNotFoundException, InvalidMethodDefinitionException, ArgsPreconditionFailedException {
        MethodManager methodManager = new MethodManager();
        Response response = EJsonResponseFixture.OK.create();
        IMethod method = EMethodFixture.HELLO.create(response);
        methodManager.addMethod(method);

        IResponse retrieved = methodManager.dispatch(getMethodName(method), new String[]{});

        assertThat(retrieved.statusCode()).isEqualTo(response.statusCode());
        assertThat(retrieved.data()).isEqualTo(response.data());
    }

    @Test
    public void overrideMethod() throws DuplicateMethodNameException, MethodNotFoundException, InvalidMethodDefinitionException, ArgsPreconditionFailedException {
        MethodManager methodManager = new MethodManager();
        Response oldResponse = EJsonResponseFixture.OK.create();
        Response newResponse = EJsonResponseFixture.OK.create();
        IMethod oldMethod = EMethodFixture.HELLO.create(oldResponse);
        IMethod newMethod = EMethodFixture.HELLO.create(newResponse);

        methodManager.addMethod(oldMethod);
        methodManager.overrideMethod(oldMethod, newMethod);

        IResponse retrieved = methodManager.dispatch(getMethodName(newMethod), new String[]{});
        assertThat(retrieved.statusCode()).isEqualTo(newResponse.statusCode());
        assertThat(retrieved.data()).isEqualTo(newResponse.data());
    }

    @Test
    public void methodExist() throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        MethodManager methodManager = new MethodManager();
        IMethod method = EMethodFixture.HELLO.create();

        assertThat(methodManager.methodExist(getMethodName(method))).isFalse();

        methodManager.addMethod(method);

        assertThat(methodManager.methodExist(getMethodName(method))).isTrue();
    }

}
