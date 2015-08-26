package org.alfresco.utilities;

import java.io.File;

import com.cobra.ldtp.Ldtp;
import org.alfresco.os.win.Application.type;
import org.alfresco.os.win.app.WindowsExplorer;

/**
 * Created by rdorobantu on 7/13/2015.
 */
public class UserActions
{
    public static void renameFile(File originalFile, File newName)
    {
        WindowsExplorer explorer = new WindowsExplorer();
        try
        {
            explorer.openApplication();
            explorer.openFolder(originalFile.getParentFile());
            explorer.rename(originalFile, newName);
            explorer.closeExplorer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File fileName)
    {
        WindowsExplorer explorer = new WindowsExplorer();
        try
        {
            explorer.openApplication();
            explorer.deleteFile(fileName, true);
            explorer.closeExplorer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * method to create a folder and file inside
     * 
     * @throws Exception
     */
    public static void createFolderAndFile(File folderName, String fileName) throws Exception
    {
        WindowsExplorer explorer = openFolder(folderName.getParentFile());
        explorer.createAndOpenFolder(folderName.getName());
        explorer.rightClickCreate(folderName.getName(), fileName, type.TEXTFILE);
        explorer.closeExplorer();
        
    }
    /**
     * open explorer and open folder    
     * @throws Exception 
     */
    
    public static WindowsExplorer openFolder(File folderName) throws Exception
    {
        WindowsExplorer explorer = new WindowsExplorer();
        explorer.openApplication();
        explorer.openFolder(folderName);
        return explorer;
    }

    public static void deleteFolder(File folderName)
    {
        WindowsExplorer explorer = new WindowsExplorer();
        try
        {
            explorer.openApplication();
            explorer.openFolder(folderName.getParentFile());
            explorer.deleteFolder(folderName, true);
            explorer.closeExplorer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * method to create a folder and file inside then go back to parent
     *
     * @throws Exception
     */
    public static WindowsExplorer createFolderAndFileAndGoBack(WindowsExplorer explorer, File folderName, String fileName) throws Exception
    {
        explorer.createAndOpenFolder(folderName.getName());
        explorer.rightClickCreate(folderName.getName(), fileName, type.TEXTFILE);
        explorer.goBack(folderName.getParentFile().getName());
        return explorer;
    }
} 