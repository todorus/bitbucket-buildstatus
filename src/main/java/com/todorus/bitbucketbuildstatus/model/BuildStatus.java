package com.todorus.bitbucketbuildstatus.model;

/**
 * Created by todorus on 10/12/15.
 */
public class BuildStatus {

  public static final String STATUS_IN_PROGRESS = "INPROGRESS";
  public static final String STATUS_SUCCESSFULL = "SUCCESSFUL";
  public static final String STATUS_FAILURE = "FAILED";


  public String status;
  public String key;
  public String name;
  public String url;
  public String description;

  public static class Links {

    public Link self;
    public Link commit;

  }

  public static class Link {
    public String href;
  }
}
