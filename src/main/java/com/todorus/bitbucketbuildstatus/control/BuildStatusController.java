package com.todorus.bitbucketbuildstatus.control;

import com.todorus.bitbucketbuildstatus.model.BuildStatus;
import hudson.scm.ChangeLogSet;

/**
 * Created by todorus on 10/12/15.
 */
public class BuildStatusController {

  private BuildStatusClient client;

  public BuildStatusController(BuildStatusClient client){
    this.client = client;
  }

  public void postStatus(BuildStatus status, String owner, String repo, String revision){
    client.createStatus(status, owner, repo, revision);
  }

}
