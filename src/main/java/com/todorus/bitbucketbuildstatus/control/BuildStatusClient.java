package com.todorus.bitbucketbuildstatus.control;

import com.todorus.bitbucketbuildstatus.model.BuildStatus;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by todorus on 11/12/15.
 */
public interface BuildStatusClient {

  //     https://api.bitbucket.org/2.0/repositories/{owner}/{repo_slug}/commit/{revision}/statuses/build/{key}
  @POST("/repositories/{owner}/{repo_slug}/commit/{revision}/statuses/build")
  BuildStatus createStatus(@Body BuildStatus buildStatus, @Path("owner") String owner, @Path("repo_slug") String repoSlug, @Path("revision") String revision);

}
