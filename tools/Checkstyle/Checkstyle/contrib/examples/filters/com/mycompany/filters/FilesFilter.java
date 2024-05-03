package com.mycompany.filters;

public class FilesFilter
    extends AutomaticBean
    implements Filter
{
    private RE mFileRegexp;

    public FilesFilter()
        throws RESyntaxException
    {
        setFiles("^$");
    }
    
    public boolean accept(AuditEvent aEvent)
    {
        final String fileName = aEvent.getFileName();
        return ((fileName == null) || !mFileRegexp.match(fileName));
    }

    public void setFiles(String aFilesPattern)
        throws RESyntaxException
    {
        mFileRegexp = Utils.getRE(aFilesPattern);
    }
}
