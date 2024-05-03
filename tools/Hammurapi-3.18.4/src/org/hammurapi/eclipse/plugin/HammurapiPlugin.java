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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Main plugin class for hammurapi code review system.
 * @version $Revision: 1.3 $
 */
public class HammurapiPlugin extends AbstractUIPlugin
{
  private static final String PLUGIN_NAME = "org.hammurapi.eclipse.plugin";
  public static final String MARKER_ID = PLUGIN_NAME + ".HammurapiMarker";
  static final String HAMMURAPI_PROPERTY = "hammurapi";
  static final String ENABLED_PROPERTY = "hammurapi.enabled";
  private static HammurapiPlugin instance;

  public HammurapiPlugin(IPluginDescriptor pluginDescriptor)
  {
    super(pluginDescriptor);
    instance = this;
  }

  public static HammurapiPlugin getInstance()
  {
    return instance;
  }

  static void report2LogError(final String msg, Throwable error)
  {
    instance.getLog().log(new Status(Status.ERROR, PLUGIN_NAME, 0, msg, error));
  }

  static void report2LogWarning(final String msg, Throwable error)
  {
    instance.getLog().log(
      new Status(Status.WARNING, PLUGIN_NAME, 0, msg, error));
  }

  static void report2LogInfo(final String msg, Throwable error)
  {
    instance.getLog().log(new Status(Status.INFO, PLUGIN_NAME, 0, msg, error));
  }

  static void report2LogOk(final String msg, Throwable error)
  {
    instance.getLog().log(new Status(Status.OK, PLUGIN_NAME, 0, msg, error));
  }

  static void clearAllMarkers(final IResource res) throws CoreException
  {
    res.deleteMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
  }

  static void createProblemMarker(
    final IResource res,
    final int lineNo,
    final int severity,
    final String message)
    throws CoreException
  {
    IMarker marker = res.createMarker(MARKER_ID);
    marker.setAttribute(IMarker.LINE_NUMBER, lineNo);
    // map to Hammurapi severity levels...
    marker.setAttribute(
      IMarker.SEVERITY,
      severity <= 1
        ? IMarker.SEVERITY_ERROR
        : severity == 2
        ? IMarker.SEVERITY_WARNING
        : IMarker.SEVERITY_INFO);
    marker.setAttribute(IMarker.MESSAGE, message);
  }
}
