package com.cooldrinkscompany.vmcs.iterator;

import javax.json.JsonObject;

public interface AbstractProductIterator {
    public JsonObject first();
    public JsonObject next();
    public boolean isDone();
}
