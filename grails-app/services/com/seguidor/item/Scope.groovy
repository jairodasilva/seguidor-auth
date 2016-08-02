package com.seguidor.item

class Scope {

    public static final TEST = { "test" == System.getenv("SCOPE") }
    public static final MASTER = { "master" == System.getenv("SCOPE") }
    public static final LOCAL = { System.getenv("SCOPE") == null }

}
