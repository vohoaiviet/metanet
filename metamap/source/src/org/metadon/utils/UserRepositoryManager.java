/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.utils;

import java.io.*;

/**
 *
 * @author Hannes
 */
public class UserRepositoryManager {

    private final static String resourceRootPathAbs = "/share/tomcat/webapps/repository/";
    private final static String resourceRootPathRel = "/repository/";
    private final static String resourcePhotosPath = "/photos/";

    public static boolean createUserRepository(String username) {
        // create resource folders for user if not existing
        System.out.println("creating user root directory...");
        File userRoot = new File(resourceRootPathAbs + username);
        
        if(userRoot.mkdirs()) {
            System.out.println("created.");
        }
        if(userRoot.canWrite()) {
            System.out.println("can write!");
            System.out.println("name: "+userRoot.getName());
        }
        if(!userRoot.isDirectory()) {
            return false;
        }

        System.out.println("creating user photo repository...");
        File userPhotoRepository = new File(resourceRootPathAbs + username + resourcePhotosPath);
        if(userPhotoRepository.mkdirs()) {
            System.out.println("created.");
        }
        if (!userPhotoRepository.isDirectory()) {
            return false;
        }
        System.out.println("repository created for: " + username);
        return true;
    }
    
    public static String getPhotoRepositoryPath(String username) {
        return resourceRootPathRel + username + resourcePhotosPath;
    }

    public static boolean deleteUserRepository(String username) {
        System.out.println("remove user repository...");
        File userRoot = new File(resourceRootPathAbs + username);
        boolean deleted = deleteDir(userRoot);
        if (deleted) {
            System.out.println("repository deleted for: " + username);
        }
        return deleted;
    }

    // deletes all files and subdirectories under dir.
    // returns true if all deletions were successful.
    // if a deletion fails, the method stops attempting to delete and returns false.
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // the directory is now empty so delete it
        return dir.delete();
    }
}
