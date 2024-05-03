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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class HammurapiPropertyPage extends PropertyPage
{

  private static final String PATH_TITLE = "Path:";

  private static final int TEXT_FIELD_WIDTH = 50;

  private Button enableButton;

  /**
   * Constructor for SamplePropertyPage.
   */
  public HammurapiPropertyPage()
  {
    super();
  }

  private void addFirstSection(Composite parent)
  {
    Composite composite = createDefaultComposite(parent);

    //Label for path field
    Label pathLabel = new Label(composite, SWT.NONE);
    pathLabel.setText(PATH_TITLE);

    // Path text field
    Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
    pathValueText.setText(((IResource)getElement()).getFullPath().toString());
  }

  private void addSeparator(Composite parent)
  {
    Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    separator.setLayoutData(gridData);
  }

  private void addSecondSection(Composite parent)
  {
    Composite composite = createDefaultComposite(parent);

    // Label for owner field
    Label ownerLabel = new Label(composite, SWT.NONE);
    ownerLabel.setText("Enabled code review:");

    // Owner text field

    enableButton = new Button(composite, SWT.CHECK);
    //new Text(composite, SWT.SINGLE | SWT.BORDER);

    GridData gd = new GridData();
    gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
    enableButton.setLayoutData(gd);

    // Populate owner text field
    try
    {
      String enabledStr =
        ((IResource)getElement()).getPersistentProperty(
          new QualifiedName(HammurapiPlugin.HAMMURAPI_PROPERTY, HammurapiPlugin.ENABLED_PROPERTY));
      enableButton.setSelection((true +"").equals(enabledStr));
    }
    catch (CoreException e)
    {
      enableButton.setSelection(false);
    }
  }

  /**
   * @see PreferencePage#createContents(Composite)
   */
  protected Control createContents(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    composite.setLayout(layout);
    GridData data = new GridData(GridData.FILL);
    data.grabExcessHorizontalSpace = true;
    composite.setLayoutData(data);

    addFirstSection(composite);
    addSeparator(composite);
    addSecondSection(composite);
    return composite;
  }

  private Composite createDefaultComposite(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    composite.setLayout(layout);

    GridData data = new GridData();
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    composite.setLayoutData(data);

    return composite;
  }

  protected void performDefaults()
  {
    // Populate the owner text field with the default value
    //enableButton.setSelection(false);
  }

  public boolean performOk()
  {
    // store the value in the owner text field
    try
    {
      ((IResource)getElement()).setPersistentProperty(
        new QualifiedName(HammurapiPlugin.HAMMURAPI_PROPERTY, HammurapiPlugin.ENABLED_PROPERTY),
        enableButton.getSelection() + "");
    }
    catch (CoreException e)
    {
      return false;
    }
    return true;
  }

}
