/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.filevinder.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A list of files referencing a trigram.
 * @author Gregory Clarke
 */
public final class Trigram implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int PRIME_A = 3, PRIME_C = 71;
    private String name;
    private ArrayList<FileRef> fileRefs;

    /**
     * Constructs an instance of the list of files referencing a trigram.
     * @param nameVar trigram value
     */
    public Trigram(final String nameVar) {
        name = nameVar;
        this.fileRefs = new ArrayList<>();
    }

    public ArrayList<FileRef> getFileRefs() {
        return fileRefs;
    }

    @Override
    public boolean equals(final Object o) {

        if (o == null) {
            return false;
        }

        if (!(o instanceof Trigram)) {
            return false;
        }

        try {
            Trigram t = (Trigram) o;
            return t.getName().equals(getName());

        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = PRIME_C * PRIME_A + Objects.hashCode(this.getName());
        hash = PRIME_C * hash + Objects.hashCode(this.getFileRefs());
        return hash;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param nameVal the name to set
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * @param fileRefsVal the fileRefs to set
     */
    public void setFileRefs(final ArrayList<FileRef> fileRefsVal) {
        this.fileRefs = fileRefsVal;
    }
}
