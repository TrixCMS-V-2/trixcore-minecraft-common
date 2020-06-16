package eu.trixcms.trixcore.common.mock;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.response.IResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodMock implements IMethod {

    @Getter
    private final String name;
    private final IResponse response;

    @Override
    public IResponse exec(String[] args) {
        return response;
    }
}
