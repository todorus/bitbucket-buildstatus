package com.todorus.bitbucketbuildstatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by todorus on 1/7/16.
 * Extracts parameters needed to identify the repository from the repository url
 */
public class RepoParamExtractor {

    private static final String SSH_MATCH = "git@bitbucket.org:[^\\/]+\\/.+(.git)";
    private static final String HTTPS_MATCH = "https:\\/\\/[^\\/]+\\/.+(.git)";

    private static final Pattern SSH_PATTERN = Pattern.compile("git@bitbucket.org:([^\\/]+)\\/(.+)\\.git");
    private static final Pattern HTTPS_PATTERN = Pattern.compile("https:\\/\\/[^\\/]+@bitbucket\\.org\\/([^\\/]+)\\/(.+)\\.git");

    public static String getSlug(String repoUrl){
        if(repoUrl.matches(SSH_MATCH)){
            Matcher matcher = SSH_PATTERN.matcher(repoUrl);
            matcher.find();
            return matcher.group(2);
        } else if(repoUrl.matches(HTTPS_MATCH)){
            Matcher matcher = HTTPS_PATTERN.matcher(repoUrl);
            matcher.find();
            return matcher.group(2);
        } else {
            return null;
        }
    }

    public static String getOwner(String repoUrl){
        if(repoUrl.matches(SSH_MATCH)){
            Matcher matcher = SSH_PATTERN.matcher(repoUrl);
            matcher.find();
            return matcher.group(1);
        } else if(repoUrl.matches(HTTPS_MATCH)){
            Matcher matcher = HTTPS_PATTERN.matcher(repoUrl);
            matcher.find();
            return matcher.group(1);
        } else {
            return null;
        }
    }

}
