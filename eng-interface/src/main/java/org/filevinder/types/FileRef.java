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
 * A List of positions within a file(ID).
 *
 * @author Gregory Clarke
 */
public final class FileRef implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int PRIME_A = 3, PRIME_B = 41;
    private Integer fileId;
    private ArrayList<Integer> positions;

    /**
     * Returns an instance of the list of positions within a file.
     * @param fileIdVar the file id
     */
    public FileRef(final Integer fileIdVar) {
        fileId = fileIdVar;
        this.positions = new ArrayList<>();
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    @Override
    public boolean equals(final Object o) {

        if (o == null || !(o instanceof FileRef)) {
            return false;
        }

        try {
            FileRef ref = (FileRef) o;
            return ref.getFileId().equals(getFileId());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return PRIME_B * PRIME_A + Objects.hashCode(this.getFileId());
    }

    /**
     * @return the fileId
     */
    public Integer getFileId() {
        return fileId;
    }

    /**
     * @param fileIdVal the fileId to set
     */
    public void setFileId(final Integer fileIdVal) {
        this.fileId = fileIdVal;
    }

    /**
     * @param positionsVal the positions to set
     */
    public void setPositions(final ArrayList<Integer> positionsVal) {
        this.positions = positionsVal;
    }
}
