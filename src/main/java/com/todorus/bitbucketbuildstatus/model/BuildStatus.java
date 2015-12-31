package com.todorus.bitbucketbuildstatus.model;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.scm.ChangeLogSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by todorus on 10/12/15.
 */
public class BuildStatus {

  public static final String STATUS_IN_PROGRESS = "INPROGRESS";
  public static final String STATUS_SUCCESSFULL = "SUCCESSFUL";
  public static final String STATUS_FAILURE = "FAILED";


  private String revision;
  private String state;
  private String key;
  private String name;
  private String url;
  private String description;
  private Links links;

  public Links getLinks() {
    return links;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public String getName() {
    return name;
  }

  public String getKey() {
    return key;
  }

  public String getState() {
    return state;
  }

  public String getRevision() {
    return revision;
  }

  /*
    HELPER CLASSES
   */

  public static class Links {

    public Link self;
    public Link commit;

  }

  public static class Link {
    public String href;
  }

  public static class Builder {

    private AbstractBuild build;
    private String rootUrl;

    public Builder setBuild(AbstractBuild build) {
      this.build = build;

      return this;
    }

    /**
     * The rootUrl of this Jenkins instance
     * @param rootUrl
     * @return
     */
    public Builder setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
        return this;
    }

    public BuildStatus build(){
      BuildStatus buildStatus = new BuildStatus();

      buildStatus.name = build.getFullDisplayName();
      buildStatus.key = build.getDisplayName();

      if(build.isBuilding()){
        buildStatus.state = STATUS_IN_PROGRESS;
      } else {
        buildStatus.state = build.getResult() == Result.SUCCESS ? STATUS_SUCCESSFULL : STATUS_FAILURE;
      }

      if (rootUrl == null) {
        buildStatus.url = " PLEASE SET JENKINS ROOT URL FROM GLOBAL CONFIGURATION " + build.getUrl();
      }
      else {
        buildStatus.url = rootUrl + build.getUrl();
      }

      List<ChangeLogSet.Entry> entries = new ArrayList<ChangeLogSet.Entry>();
      Iterator<ChangeLogSet.Entry> iterator = build.getChangeSet().iterator();
      while(iterator.hasNext()){
        entries.add(iterator.next());
      }
      if(entries.size() > 0){
        buildStatus.revision = entries.get(entries.size()-1).getCommitId();
      }

      return buildStatus;
    }
  }
}
