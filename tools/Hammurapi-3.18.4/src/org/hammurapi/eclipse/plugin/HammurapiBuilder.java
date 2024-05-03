/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Hammurapi Group
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * URL: http://www.hammurapi.org
 * e-Mail: support@hammurapi.biz

 */
package org.hammurapi.eclipse.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorSource;
import org.hammurapi.ReviewRequestBase;
import org.hammurapi.Reviewer;
import org.hammurapi.Violation;

/**
 * Builder class for hammurapi code review system.
 * @author zorror8080
 * @version $Revision: 1.5 $
 */
public class HammurapiBuilder extends IncrementalProjectBuilder
{
  private void performBuild(IProgressMonitor progressMonitor) throws CoreException
  {
    final IProject project = getProject();
    HammurapiPlugin.report2LogInfo("project: " + project, null);

    if (project != null)
    {
      HammurapiPlugin.report2LogInfo("proj.name: " + project.getName(), null);
      HammurapiPlugin.report2LogInfo("proj.isOpen(): " + project.isOpen(), null);
      HammurapiPlugin.report2LogInfo("project.hasNature: " + project.hasNature("HammurapiNature"), null);
      Collection files = null;
      IResourceDelta rDelta = getDelta(project);
      HammurapiPlugin.report2LogInfo("rDelta: " + rDelta, null);
      if (rDelta != null)
      {
        files = getFiles(rDelta);
      }
      else
      {
        files = getFiles(project);
      }
      HammurapiPlugin.report2LogInfo("files: " + files, null);
      HammurapiPlugin.report2LogInfo("files.size(): " + files.size(), null);
      if (files.size() > 0)
      {
        progressMonitor.beginTask("Hammurapi", 100); //files.size());
        try
        {
          Reviewer reviewer = null;
          try
          {
            reviewer = new Reviewer(null, true, (InspectorSource) null, null);
          }
          catch (HammurapiException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

          ClassLoader classLoader = null;
          IJavaProject javaProject = (IJavaProject)project.getAdapter(IJavaElement.class);
          try
          {
            List urls = getClasspathURLs(javaProject, false);
            URL[] urlArray = (URL[])urls.toArray(new URL[urls.size()]);
            classLoader = new URLClassLoader(urlArray);
          }
          catch (CoreException e)
          {
            throw e;
          }
          catch (Throwable e)
          {
            HammurapiPlugin.report2LogError("Unable to create a classloader for the project", e);
            classLoader = null;
          }

          IFile file;
          IMarker marker;
          HammurapiPlugin.clearAllMarkers(project);
          HammurapiPlugin.report2LogInfo("markers deleted.", null);
          HammurapiChecker hChecker;
          final List fileRef = new ArrayList();
          for (Iterator it = files.iterator(); it.hasNext();)
          {
            file = (IFile)it.next();
            fileRef.add(file);
            progressMonitor.subTask(file.getName());
            // test...
            /*
                        HammurapiPlugin.createProblemMarker(
                          file,
                          9,
                          IMarker.SEVERITY_ERROR,
                          "hello!");
                        */
            // kick Hammurapi code check...and collect result.
            try
            {
              HammurapiPlugin.report2LogInfo("reviewer: " + reviewer, null);

              reviewer.process(new ReviewRequestBase(null)
              {
                public Reader getSource()
                {
                  Reader res = null;
                  try
                  {
                    res = new BufferedReader(new InputStreamReader(((IFile)fileRef.get(0)).getContents()));
                  }
                  catch (CoreException e)
                  {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  HammurapiPlugin.report2LogInfo("getSource: " + res, null);

                  return res;
                }

                public String getName()
                {
                  return ((IFile)fileRef.get(0)).getFullPath().toString();
                }

                public void onViolation(Violation violation)
                {
                  try
                  {
                    HammurapiPlugin.createProblemMarker(
                      (IResource)fileRef.get(0),
                      (violation.getSource() == null ? 0 : violation.getSource().getLine()),
                      IMarker.SEVERITY_ERROR,
                      violation.getMessage());
                  }
                  catch (CoreException ex)
                  {
                  }
                }

                public void onWarning(Violation warning)
                {
                  try
                  {
                    HammurapiPlugin.createProblemMarker(
                      (IResource)fileRef.get(0),
                      (warning.getSource() == null ? 0 : warning.getSource().getLine()),
                      IMarker.SEVERITY_WARNING,
                      warning.getMessage());
                  }
                  catch (CoreException ex)
                  {
                  }
                }

				public File getRootDir() {
					// TODO Auto-generated method stub
					return null;
				}
              });
            }
            catch (HammurapiException e1)
            {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
            if (progressMonitor.isCanceled())
            {
              forgetLastBuiltState();
              throw new OperationCanceledException();
            }
            /*
            hChecker = new HammurapiChecker(reviewer);
            new Thread(hChecker).start();
            while (!hChecker.isCompleted())
            {
            
              //HammurapiPlugin.createProblemMarker(file, 9, IMarker.SEVERITY_ERROR, "hello!");
              if (progressMonitor.isCanceled())
              {
                hChecker.stop(); // stop checker...
                forgetLastBuiltState();
                throw new OperationCanceledException();
              }
            }
            */
            /*
            if (i > 100)
            {
              throw new CoreException(new Status(IStatus.INFO,"hammurapi plugin",0,"reached 100", null));
            }
            */
            progressMonitor.worked(1);
          }
        }
        finally
        {
          progressMonitor.done();
        }
      }
    }
  }

  /**
   *  Determine if running eclipse 2.1 or newer.
   *
   *  The ability for source folders to have different output folders
   *  was introduced in Eclipse 2.1.  Check the version of the
   *  org.eclipse.jdt.core plug-in to determine if it's safe to
   *  call the getOutputLocation() method, which was introduced
   *  in Eclipse v2.1.
   */
  private boolean isEclipse_2_1_Safe()
  {
    boolean res = false;
    //if (!mEclipseVersionDetermined)
    {
      Plugin jdtCorePlugin = Platform.getPlugin("org.eclipse.jdt.core");
      if (jdtCorePlugin != null)
      {
        PluginVersionIdentifier version = jdtCorePlugin.getDescriptor().getVersionIdentifier();
        res = version.isGreaterOrEqualTo(new PluginVersionIdentifier("2.1"));
      }
      //mEclipseVersionDetermined = true;
    }

    return res;
  }

  /**
   * @author Daniel Berg jdt-dev@eclipse.org.
   * @param javaProject
   * @return
   */
  private List getClasspathURLs(IJavaProject javaProject, boolean exportedOnly)
    throws JavaModelException, MalformedURLException, CoreException
  {
    HashSet urls = new HashSet();

    IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
    boolean defaultOutputAdded = false;

    for (int i = 0; i < entries.length; i++)
    {
      // Source entries are apparently always assumed to be exported - but don't
      // report themselves as such.
      if (!exportedOnly
        || entries[i].isExported()
        || entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE)
      {
        switch (entries[i].getEntryKind())
        {
          case IClasspathEntry.CPE_SOURCE :
            {
              IPath outputLocation = null;

              if (isEclipse_2_1_Safe())
              {
                outputLocation = entries[i].getOutputLocation();
              }
              if (outputLocation == null)
              {
                //
                //  If the output location is null then the project's
                //  default output location is being used.
                //
                if (!defaultOutputAdded)
                {
                  defaultOutputAdded = true;
                  outputLocation = javaProject.getOutputLocation();
                }
              }

              if (outputLocation != null)
              {

                // When the output location is the project itself, the project
                // can't resolve the file - therefore just get the project's
                // location. 

                if (outputLocation.segmentCount() == 1)
                {
                  outputLocation = javaProject.getProject().getLocation();
                }
                else
                {
                  // Output locations are always workspace relative. Do this mess
                  // to get a fully qualified location.
                  outputLocation = javaProject.getProject().getParent().getFile(outputLocation).getLocation();
                }

                urls.add(outputLocation.addTrailingSeparator().toFile().toURL());
              }

              break;
            }
          case IClasspathEntry.CPE_LIBRARY :
            {
              // Jars always come with a nice fully specified path.
              urls.add(new URL("file:/" + entries[i].getPath().toOSString()));

              break;
            }
          case IClasspathEntry.CPE_PROJECT :
            {
              IJavaProject dependentProject =
                (IJavaProject)
                  (
                    ResourcesPlugin.getWorkspace().getRoot().getProject(
                      entries[i].getPath().segment(0))).getAdapter(
                  IJavaElement.class);

              urls.addAll(getClasspathURLs(dependentProject, true));

              break;
            }
          default :
            {
              String msg = "Encountered unexpected classpath entry : " + entries[i].getEntryKind();
              HammurapiPlugin.report2LogError(msg, null);
              Status status = new Status(IStatus.ERROR, "HammurapiPlugin", IStatus.ERROR, msg, null);
              throw new CoreException(status);
            }
        }
      }
    }

    return new ArrayList(urls);
  }

  private Collection getFiles(final IResourceDelta rDelta) throws CoreException
  {
    List files = new ArrayList();
    List folders = new ArrayList();
    IResourceDelta rDeltas[] = rDelta.getAffectedChildren();
    //HammurapiPlugin.report2LogInfo("rDeltas: "+rDeltas, null);
    //HammurapiPlugin.report2LogInfo("rDeltas.size: "+rDeltas.length, null);
    IResourceDelta currDelta;
    IResource currResource;
    int deltaKind;
    int resourceType;
    for (int i = 0; i < rDeltas.length; i++)
    {
      currDelta = rDeltas[i];
      currResource = currDelta.getResource();
      resourceType = currResource.getType();
      //HammurapiPlugin.report2LogInfo("resourceType: "+resourceType, null);
      //HammurapiPlugin.report2LogInfo("currResource.getName(): "+currResource.getName(), null);
      if (resourceType == IResource.FILE)
      {
        deltaKind = currDelta.getKind();
        if ((deltaKind == IResourceDelta.ADDED || deltaKind == IResourceDelta.CHANGED)
          && currResource.getName() != null
          && currResource.getName().endsWith(".java"))
        {
          files.add(currResource);
        }
      }
      else
      {
        if (resourceType == IResource.FOLDER)
        {
          folders.add(currDelta);
        }
      }
    }
    for (Iterator it = folders.iterator(); it.hasNext();)
    {
      files.addAll(getFiles((IResourceDelta)it.next()));
    }
    return files;
  }

  private Collection getFiles(IContainer container) throws CoreException
  {
    List files = new ArrayList();
    List folders = new ArrayList();
    int rType;
    IResource resource;
    IResource[] resources = container.members();
    for (int i = 0; i < resources.length; i++)
    {
      resource = resources[i];
      rType = resource.getType();
      if (rType == IResource.FILE && resource.getName() != null && resource.getName().endsWith(".java"))
      {
        files.add(resource);
      }
      else
      {
        if (rType == IResource.FOLDER)
        {
          folders.add(resource);
        }
      }
    }

    for (Iterator it = folders.iterator(); it.hasNext();)
    {
      files.addAll(getFiles((IContainer)it.next()));
    }
    return files;
  }

  protected IProject[] build(int buildType, Map map, IProgressMonitor progressMonitor) throws CoreException
  {
    HammurapiPlugin.report2LogInfo(
      "@@@prop: "
        + getProject().getPersistentProperty(
          new QualifiedName(HammurapiPlugin.HAMMURAPI_PROPERTY, HammurapiPlugin.ENABLED_PROPERTY)),
      null);
    if ((true +"")
      .equals(
        getProject().getPersistentProperty(
          new QualifiedName(HammurapiPlugin.HAMMURAPI_PROPERTY, HammurapiPlugin.ENABLED_PROPERTY))))
    {
      switch (buildType)
      {
        case IncrementalProjectBuilder.AUTO_BUILD :
          HammurapiPlugin.report2LogInfo("doAutoBuild", null);
          performBuild(progressMonitor);
          break;

        case IncrementalProjectBuilder.INCREMENTAL_BUILD :
          HammurapiPlugin.report2LogInfo("doIncrementalBuild", null);
          performBuild(progressMonitor);
          break;

        default :
          HammurapiPlugin.report2LogInfo("doFullBuild", null);
          performBuild(progressMonitor);
          break;
      }
    }
    return null;
  }

  public static class BuildRunnable implements IRunnableWithProgress
  {
    private List projects2Build;

    private BuildRunnable()
    {
      projects2Build = new ArrayList();
    }

    void addProject(IProject iproject)
    {
      projects2Build.add(iproject);
    }

    public void run(org.eclipse.core.runtime.IProgressMonitor mon)
      throws java.lang.reflect.InvocationTargetException, java.lang.InterruptedException
    {
      IProject aProject;
      for (Iterator iterator = projects2Build.iterator(); iterator.hasNext();)
      {
        aProject = (org.eclipse.core.resources.IProject)iterator.next();
        try
        {
          aProject.build(6, HammurapiBuilder.class.getName(), null, mon);
        }
        catch (CoreException e)
        {
          throw new InvocationTargetException(e);
        }
      }
    }
  }

  private static class HammurapiChecker implements Runnable
  {
    private boolean stopRequested = false;
    private boolean completed = false;
    private int severity;
    private int lineNo;
    private String reviewMessage;
    private Reviewer reviewer;

    private HammurapiChecker(Reviewer reviewer)
    {
      this.reviewer = reviewer;
    }

    void stop()
    {
      stopRequested = true;
    }

    boolean isCompleted()
    {
      return completed;
    }

    int getSeverity()
    {
      return severity;
    }

    int getLineNo()
    {
      return lineNo;
    }

    String getReviewMessage()
    {
      return reviewMessage;
    }

    public void run()
    {
      try
      {
        while (!stopRequested)
        {
          //reviewer.process(null);
        }
      }
      finally
      {
        completed = true;
      }
    }
  }
}
