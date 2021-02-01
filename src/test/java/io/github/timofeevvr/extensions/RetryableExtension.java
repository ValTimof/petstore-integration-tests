package io.github.timofeevvr.extensions;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Allows to do reties on failed tests.
 */
@Slf4j
public class RetryableExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        retryInvocation(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation,
                                            ReflectiveInvocationContext<Method> invocationContext,
                                            ExtensionContext extensionContext) throws Throwable {
        retryInvocation(invocation, invocationContext, extensionContext);
    }

    private void retryInvocation(Invocation<Void> invocation,
                                 ReflectiveInvocationContext<Method> invocationContext,
                                 ExtensionContext extensionContext) throws Throwable {
        try {
            invocation.proceed();
        } catch (Throwable t) {
            retry(invocationContext, t, extensionContext, 1, getRetries(extensionContext));
        }
    }

    private void retry(ReflectiveInvocationContext<Method> invocationContext,
                       Throwable error,
                       ExtensionContext extensionContext,
                       int retry, final int maxRetries) throws Throwable {
        log.error("{} test failed after {} attempt. Max attempts are {}",
                extensionContext.getDisplayName(), retry, maxRetries + 1);
        if (retry > maxRetries) {
            throw error;
        }
        try {
            Optional<Object> target = invocationContext.getTarget();
            if (target.isPresent()) {
                invocationContext.getExecutable().invoke(target.get(), invocationContext.getArguments().toArray());
            } else {
                throw new IllegalStateException("invocationContext target is not present " + invocationContext);
            }
        } catch (Throwable t) {
            t = t.getCause() != null ? t.getCause() : t;
            retry(invocationContext, t, extensionContext, ++retry, maxRetries);
        }
    }

    private int getRetries(ExtensionContext extensionContext) {
        return extensionContext.getTestMethod()
                .map(method -> method.getAnnotation(Retryable.class).value())
                .orElse(0);
    }
}
