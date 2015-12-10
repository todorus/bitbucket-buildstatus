package com.todorus.bitbucketbuildstatus.model;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * Created by todorus on 10/12/15.
 */
public class BuildStatusTest {

  @Mock
  AbstractBuild abstractBuild;

  @Mock
  User user;

  @Mock
  ChangeLogSet<TestEntry> changeLogSet;

  String displayName;
  String buildId;
  String buildUrl;
  String rootUrl;

  List<TestEntry> changes;
  ChangeLogSet.Entry lastEntry;

  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);

    displayName = UUID.randomUUID().toString();
    buildId = UUID.randomUUID().toString();
    buildUrl = UUID.randomUUID().toString();
    rootUrl = UUID.randomUUID().toString();

    stub(abstractBuild.getDisplayName()).toReturn(displayName);
    stub(abstractBuild.getId()).toReturn(buildId);
    stub(abstractBuild.getUrl()).toReturn(buildUrl);

    changes = new ArrayList<TestEntry>();
    changes.add(new TestEntry(UUID.randomUUID().toString(), UUID.randomUUID().toString(), user, null));
    changes.add(new TestEntry(UUID.randomUUID().toString(), UUID.randomUUID().toString(), user, null));
    changes.add(new TestEntry(UUID.randomUUID().toString(), UUID.randomUUID().toString(), user, null));
    changes.add(new TestEntry(UUID.randomUUID().toString(), UUID.randomUUID().toString(), user, null));
    lastEntry = changes.get(changes.size()-1);

    stub(changeLogSet.iterator()).toReturn(changes.iterator());
    stub(abstractBuild.getChangeSet()).toReturn(changeLogSet);
  }

  @Test
  public void inProgressStatus() throws Exception {
    stub(abstractBuild.getResult()).toReturn(Result.NOT_BUILT);
    stub(abstractBuild.isBuilding()).toReturn(true);

    BuildStatus buildStatus = new BuildStatus.Builder()
            .setRootUrl(rootUrl)
            .setBuild(abstractBuild)
            .build();

    assertEquals(lastEntry.getCommitId(), buildStatus.getRevision());
    assertEquals(displayName, buildStatus.getName());
    assertEquals(buildId, buildStatus.getKey());
    assertEquals(rootUrl + buildUrl, buildStatus.getUrl());
    assertEquals(BuildStatus.STATUS_IN_PROGRESS, buildStatus.getStatus());
  }

  @Test
  public void successStatus() throws Exception {
    stub(abstractBuild.getResult()).toReturn(Result.SUCCESS);
    stub(abstractBuild.isBuilding()).toReturn(false);

    BuildStatus buildStatus = new BuildStatus.Builder()
            .setRootUrl(rootUrl)
            .setBuild(abstractBuild)
            .build();

    assertEquals(lastEntry.getCommitId(), buildStatus.getRevision());
    assertEquals(displayName, buildStatus.getName());
    assertEquals(buildId, buildStatus.getKey());
    assertEquals(rootUrl+buildUrl, buildStatus.getUrl());
    assertEquals(BuildStatus.STATUS_SUCCESSFULL, buildStatus.getStatus());
  }

  @Test
  public void failedStatus() throws Exception {
    stub(abstractBuild.getResult()).toReturn(Result.FAILURE);
    stub(abstractBuild.isBuilding()).toReturn(false);

    BuildStatus buildStatus = new BuildStatus.Builder()
            .setRootUrl(rootUrl)
            .setBuild(abstractBuild)
            .build();

    assertEquals(lastEntry.getCommitId(), buildStatus.getRevision());
    assertEquals(displayName, buildStatus.getName());
    assertEquals(buildId, buildStatus.getKey());
    assertEquals(rootUrl+buildUrl, buildStatus.getUrl());
    assertEquals(BuildStatus.STATUS_FAILURE, buildStatus.getStatus());
  }

  private static class TestEntry extends ChangeLogSet.Entry {

    private String commitId;
    private String message;
    private User author;
    private Collection<String> affectedPaths;


    public TestEntry(String commitId, String message, User author, Collection<String> affectedPaths) {
      this.commitId = commitId;
      this.message = message;
      this.author = author;
      this.affectedPaths = affectedPaths;
    }

    @Override
    public String getMsg() {
      return message;
    }

    @Override
    public User getAuthor() {
      return author;
    }

    @Override
    public Collection<String> getAffectedPaths() {
      return affectedPaths;
    }

    @Override
    public String getCommitId() {
      return commitId;
    }
  }
}