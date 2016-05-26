package com.bitcup.shoppee.storage;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

/**
 * @author bitcup
 */
public class ShoppeeStore {
    private String id;
    private String name;

    // do not remove - used by jackson
    public ShoppeeStore() {
    }

    public ShoppeeStore(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .toString();
    }
}
