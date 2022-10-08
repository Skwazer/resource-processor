package com.epam.resourceprocessor.processor;


public enum MetadataProperty {
    TITLE("dc:title"),
    ARTIST("xmpDM:artist"),
    ALBUM("xmpDM:album"),
    RELEASE_YEAR("xmpDM:releaseDate"),
    DURATION("xmpDM:duration");

    private final String name;

    private MetadataProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
