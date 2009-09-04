/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.control;

import java.util.Stack;

import org.metadon.beans.Blog;

/**
 *
 * @author Hannes
 */
public abstract class BlogBrowser {
    
    abstract void init();
    abstract void loadBlogs();
    
    public void openSelectedBlog(){};
    public void postSelectedBlog(){};
    public void deleteSelectedBlog(){};
    
    public void resolveJourney(){};
    public void postJourney(){};
    public void deleteJourney(){};
    
    public void clear(){};
    
    // sort the blogs stored in a blog set in ascending timestamp order;
    // return the sorted set
    public Stack sortBlogSet(Stack blogSet) {

        Object[] blogSetArray = new Object[blogSet.size()];
        blogSet.copyInto(blogSetArray);

        // bubble sort algorithm
        int n = blogSetArray.length;
        Object temp;
        for (int i = 0; i < n - 1; i = i + 1) {
            for (int j = n - 1; j > i; j = j - 1) {
                if (((Blog) blogSetArray[j - 1]).getTimestamp() >
                        ((Blog) blogSetArray[j]).getTimestamp()) {
                    temp = blogSetArray[j - 1];
                    blogSetArray[j - 1] = blogSetArray[j];
                    blogSetArray[j] = temp;
                }
            }
        }
        for (int i = 0; i < blogSetArray.length; i++) {
            blogSet.setElementAt(blogSetArray[i], i);
        }
        return blogSet;
    }
    
}
