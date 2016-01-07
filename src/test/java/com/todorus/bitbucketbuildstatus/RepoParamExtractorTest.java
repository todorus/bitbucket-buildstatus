package com.todorus.bitbucketbuildstatus;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by todorus on 1/7/16.
 */
public class RepoParamExtractorTest {

    private final String OWNER = "some-owner";
    private final String SLUG = "some_slug";

    private final String SSH_URL = "git@bitbucket.org:"+OWNER+"/"+SLUG+".git";
    private final String HTTPS_URL = "https://some_user_name@bitbucket.org/"+OWNER+"/"+SLUG+".git";
    private final String GARBAGE = "git@blobbucket.org:"+OWNER+"/"+SLUG+".blob";

    @Test
    public void testGetSlug() throws Exception {
        assertEquals(SLUG, RepoParamExtractor.getSlug(SSH_URL));
        assertEquals(SLUG, RepoParamExtractor.getSlug(HTTPS_URL));
        assertEquals(null, RepoParamExtractor.getSlug(GARBAGE));
    }

    @Test
    public void testGetOwner() throws Exception {
        assertEquals(OWNER, RepoParamExtractor.getOwner(SSH_URL));
        assertEquals(OWNER, RepoParamExtractor.getOwner(HTTPS_URL));
        assertEquals(null, RepoParamExtractor.getOwner(GARBAGE));
    }
}
