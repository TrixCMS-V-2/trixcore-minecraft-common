package eu.trixcms.trixcore.common.fixtures;

import eu.trixcms.trixcore.common.i18n.MessageRepository;
import eu.trixcms.trixcore.common.mock.MessageSourceMock;

public class MessageSourceFixture {

    public static MessageSourceMock cast(MessageRepository repository){
        return (MessageSourceMock) repository.getSource();
    }

}
