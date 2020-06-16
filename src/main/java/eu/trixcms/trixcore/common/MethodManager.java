package eu.trixcms.trixcore.common;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.IMethodsManager;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.ArgsPrecondition;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.method.exception.ArgsPreconditionFailedException;
import eu.trixcms.trixcore.api.method.exception.DuplicateMethodNameException;
import eu.trixcms.trixcore.api.method.exception.InvalidMethodDefinitionException;
import eu.trixcms.trixcore.api.method.exception.MethodNotFoundException;
import eu.trixcms.trixcore.api.response.IResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class MethodManager implements IMethodsManager {

    @Getter
    private Map<String, IMethod> methods = new HashMap<>();

    private String getMethodName(IMethod method) {
        try {
            MethodName methodName = method.getClass().getAnnotation(MethodName.class);
            if (!methodName.name().equals("none")) {
                return methodName.name();
            } else if (!methodName.method().equals(Methods.NONE)) {
                return methodName.method().getSlug();
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addMethod(IMethod method) throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        if (!method.getClass().isAnnotationPresent(MethodName.class))
            throw new InvalidMethodDefinitionException(method.getClass());

        String name = getMethodName(method);

        if (name == null)
            throw new InvalidMethodDefinitionException(method.getClass());

        if (methodExist(name))
            throw new DuplicateMethodNameException();

        methods.put(name, method);
    }

    @Override
    public void removeMethod(IMethod method) {
        String name = getMethodName(method);
        if (name != null)
            removeMethod(name);
    }

    @Override
    public void removeMethod(String methodName) {
        if (methodExist(methodName))
            methods.remove(methodName);
    }

    @Override
    public void overrideMethod(IMethod oldIMethod, IMethod newIMethod) throws InvalidMethodDefinitionException {
        String name = getMethodName(oldIMethod);
        if (name != null)
            overrideMethod(name, newIMethod);
    }

    @Override
    public void overrideMethod(String oldMethodName, IMethod newIMethod) throws InvalidMethodDefinitionException {
        removeMethod(oldMethodName);

        try {
            addMethod(newIMethod);
        } catch (DuplicateMethodNameException ignored) {
        }
    }

    @Override
    public boolean methodExist(String methodName) {
        return (methods.containsKey(methodName));
    }

    @Override
    public IResponse dispatch(String methodName, String[] args) throws MethodNotFoundException, ArgsPreconditionFailedException {
        if (!methodExist(methodName))
            throw new MethodNotFoundException(methodName);

        IMethod method = methods.get(methodName);

        try {
            Method fMethod = method.getClass().getMethod("exec", String[].class);
            if (fMethod.isAnnotationPresent(ArgsPrecondition.class)) {
                int amount = fMethod.getAnnotation(ArgsPrecondition.class).amount();
                int min = fMethod.getAnnotation(ArgsPrecondition.class).min();
                int max = fMethod.getAnnotation(ArgsPrecondition.class).max();

                if (amount == 0 && (min != 0 || max != 0)) {
                    if (max != 0 && args.length > max) {
                        throw new ArgsPreconditionFailedException(args.length + " < " + max);
                    }

                    if (min != 0 && args.length < min) {
                        throw new ArgsPreconditionFailedException(args.length + " > " + min);
                    }
                } else if (amount != 0 && args.length != amount) {
                    throw new ArgsPreconditionFailedException(args.length + " != " + amount);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return method.exec(args);
    }

    @Override
    public void addMethods(List<IMethod> methods) throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        for (IMethod method : methods)
            addMethod(method);
    }
}
