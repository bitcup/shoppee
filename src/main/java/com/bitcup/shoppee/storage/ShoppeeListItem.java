package com.bitcup.shoppee.storage;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author bitcup
 */
public class ShoppeeListItem {
    private String name;
    private boolean purchased = false;

    // do not remove - used by jackson
    public ShoppeeListItem() {
    }

    public ShoppeeListItem(String name) {
        this.name = name;
    }

    public ShoppeeListItem(String name, boolean purchased) {
        this.name = name;
        this.purchased = purchased;
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
                .append("name", name)
                .append("purchased", purchased)
                .toString();
    }
}
