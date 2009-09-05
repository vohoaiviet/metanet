package org.metadon.metaservice.util;

import java.io.*;

/**
 *
 * @author Hannes
 */
public class UserRepositoryManager {

    private final static String resourceRootPath = "/share/tomcat/webapps/repository/";
    private final static String resourcePhotoPath = "/photos/";

    public static String getPhotoRepositoryPath(String username) {
        return resourceRootPath + username + resourcePhotoPath;
    }

    public static boolean isExistingRepository(String username) {
        System.out.println("look for repository...");
        File userPhotoRepository = new File(getPhotoRepositoryPath(username));
        System.out.println("looking for: "+userPhotoRepository.getPath());
        if (!userPhotoRepository.isDirectory()) {
            //return false;
        }
        return true;
    }
}
