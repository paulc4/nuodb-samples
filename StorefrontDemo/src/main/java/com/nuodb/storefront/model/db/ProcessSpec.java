/* Copyright (c) 2013-2015 NuoDB, Inc. */

package com.nuodb.storefront.model.db;

public class ProcessSpec {
    public String type;
    public String host;
    public String dbname;
    public boolean initialize;
    public boolean overwrite;
    public String archive;
}