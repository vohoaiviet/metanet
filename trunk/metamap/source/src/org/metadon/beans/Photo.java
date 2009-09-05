/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.beans;

public class Photo {

    int id;
    String name;
    int size;
    int width;
    int height;

    // id
    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
    
    // name
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    
     // size
    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    // photo width
    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }
    
    // photo height
    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }
}
