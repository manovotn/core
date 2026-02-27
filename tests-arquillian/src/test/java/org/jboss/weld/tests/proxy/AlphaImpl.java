package org.jboss.weld.tests.proxy;

public class AlphaImpl implements Alpha<String> {

    @Override
    public Alpha<String> returnSelf() {
        return this;
    }
}
