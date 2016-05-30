package com.bitcup.shoppee.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

/**
 * @author bitcup
 */
public class ShoppeeListItem {
    private String id;
    private String name;
    private boolean purchased = false;

    // do not remove - used by jackson
    public ShoppeeListItem() {
    }

    public ShoppeeListItem(String name) {
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

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("purchased", purchased)
                .toString();
    }
}
