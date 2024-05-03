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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;

public class HammurapiNature implements IProjectNature
{
  private IProject project;

  public void setProject(IProject project)
  {
    this.project = project;
    HammurapiPlugin.report2LogInfo("setProject", null);
  }

  public IProject getProject()
  {
    HammurapiPlugin.report2LogInfo("getProject", null);
    return project;
  }

  public void configure()
  {
    HammurapiPlugin.report2LogInfo("configure", null);
  }

  public void deconfigure()
  {
    HammurapiPlugin.report2LogInfo("deconfigure", null);
  }
}
