/*
* Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.os.win.app;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.alfresco.os.common.ApplicationBase;
import org.alfresco.os.win.Application;
import org.alfresco.os.win.app.office.MicrosoftOfficeBase.VersionDetails;
import org.alfresco.utilities.LdtpUtils;
import org.apache.log4j.Logger;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;
import com.google.common.io.Files;

/**
 * * This class will handle Windows based operation over Windows Explorer
 * As a Windows OS environment settings, please just turn off "Hide extension for known file types"
 * 
 * @author Subashni Prasanna
 * @author Paul Brodner
 */
public class WindowsExplorer extends Application
{
    private static Logger logger = Logger.getLogger(WindowsExplorer.class);

    public WindowsExplorer()
    {
        setApplicationName("explorer.exe");
        setApplicationPath(LdtpUtils.getDocumentsFolder().getParentFile().getPath());

        if (LdtpUtils.isWin81())
        {
            setWaitWindow("This PC");
        }
        else if(LdtpUtils.isWin10())
        {
        	setWaitWindow("File Explorer");
        }
        else
        {
            setWaitWindow("Libraries");
        }
    }

    /**
     * Focus a file name using <windowName> passes as parameter
     * 
     * @param windowName
     */
    public void focus(String windowName)
    {
        getLdtp().setWindowName(windowName);
    }

    public void focus()
    {
        getLdtp().setWindowName(getWaitWindow());
       // getLdtp().activateWindow(getWaitWindow());
    }

    @Override
    public ApplicationBase openApplication()
    {
        super.openApplication();
        maximize();
        return this;
    }

    /**
     * Method to open a particular folder in windows explorer
     * 
     * @param folderPath path of the folder as String
     * @throws Exception
     * @throws InterruptedException
     */
    public void openFolder(File folderPath) throws Exception
    {
        LdtpUtils.logInfo("open folder in the " + folderPath.getPath());
        if (!folderPath.exists())
        {
            throw new IOException("Folder does not exists: " + folderPath.getPath());
        }
        if (!folderPath.isDirectory())
        {
            throw new IOException("Please provide a folder");
        }
        if (LdtpUtils.isWin81())
        {
        	getLdtp().grabFocus("This PC");
        	getLdtp().waitTime(1);
            getLdtp().generateKeyEvent("<alt>d"); // focusing address editor
            getLdtp().generateKeyEvent(folderPath.getPath());
        }
        else if(LdtpUtils.isWin10())
        {
        	getLdtp().grabFocus("Quick access");
        	getLdtp().waitTime(1);
            getLdtp().generateKeyEvent("<alt>d"); // focusing address editor
            getLdtp().generateKeyEvent(folderPath.getPath());
        }
        else
        {
            getLdtp().mouseLeftClick("pane2");
            try
            {
                getLdtp().enterString("uknLibraries", "");
            }
            catch (Exception e)
            {
            }
            getLdtp().generateKeyEvent("<alt>d"); // focusing address editor
            getLdtp().generateKeyEvent(folderPath.getPath());
        }
        getLdtp().keyPress("<enter>");
        getLdtp().setWindowName(folderPath.getName());
    }

    /**
     * Create a new folder from the new folder option in the menu of windows explorer.
     * This method assume that you have already have explorer opened and you have navigated to the particular folder
     * to create a folder inside it.
     * 
     * @param - Name of the folder to create.
     */
    public void createNewFolderMenu(String folderName)
    {
        logger.info("click on new folder and enter the folder name");
        getLdtp().click("New folder");
        getLdtp().waitTime(1);
        getLdtp().generateKeyEvent(folderName);
        getLdtp().keyPress("<enter>");
        getLdtp().waitTime(1);
    }

    /**
     * Create a new folder from the new folder option in the menu of windows explorer.
     * This method assume that you have already have explorer opened and you have navigated to the particular folder
     * to create a folder inside it. Upon successful create the folder will be opened
     * 
     * @param - Name of the folder to create String
     */
    public String createAndOpenFolder(String folderName)
    {
        createNewFolderMenu(folderName);
        getLdtp().waitTime(2);
        return openFolderFromCurrent(folderName);
    }

    /**
     * This method will help us to open a particular folder assuming you have already browsed to the location
     * 
     * @param - String Folder name to open
     */
    public String openFolderFromCurrent(String folderName) throws LdtpExecutionError
    {
        logger.info("open the folder in the current location " + folderName);
        getLdtp().doubleClick(folderName);
        getLdtp().setWindowName(folderName);
        return folderName;
    }

    /**
     * set window name for the opened file
     * 
     * @throws InterruptedException
     */
    public void activateApplicationWindow(String name)
    {
        String _name = LdtpUtils.getFullWindowList(getLdtp(), name);
        focus(_name);
        getLdtp().activateWindow(_name);
    }

    /**
     * Set the name and password on Windows Security
     * 
     * @param userName
     * @param password
     * @throws Exception
     */
    public void operateOnSecurity(String userName, String password) throws Exception
    {
        if (waitForWindow("Windows Security") != null)
        {
            getLdtp().deleteText("txtUsername", 0);
            getLdtp().enterString("txtUsername", userName);
            getLdtp().enterString("txtPassword", password);
            getLdtp().click("OK");
        }
    }

    /**
     * open a file from the windows explorer based on the give path
     * 
     * @throws Exception
     */
    public void openFile(File file) throws Exception
    {
        openFolder(file.getParentFile());
        openFileInCurrentFolder(file);
    }

    /**
     * Close explorer window
     */
    public void closeExplorer()
    {
        logger.info("close the explorer");
        getLdtp().click("Close");
        getLdtp().waitTime(2);
        setLdtp(null);

    }

    @Override
    public void exitApplication()
    {
        closeExplorer();
    }

    /**
     * Open a file in a particular folder - Assume we are already in that folder and we trying to open the file
     * 
     * @param - String - file to open
     */
    public void openFileInCurrentFolder(File file) throws LdtpExecutionError
    {
        logger.info("open the file present in the current folder " + file.getName());
        getLdtp().doubleClick(file.getName());
    }

    /**
     * Delete a file or folder in a given path
     * 
     * @throws Exception
     */
    public void deleteFile(File fileToDelete, boolean confirmationOption) throws Exception
    {
        logger.info("open a particular folder to delete " + fileToDelete.getAbsolutePath());
        openFolder(fileToDelete.getParentFile());
        delete(fileToDelete.getName(), confirmationOption);        
    }

    /**
     * Delete a file or folder in a given path
     * 
     * @throws Exception
     */
    public void deleteFile(File fileToDelete) throws Exception
    {
        deleteFile(fileToDelete, true);
    }

    /**
     * Delete a folder
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void deleteFolder(File folderName, boolean areYouSure) throws Exception
    {
        logger.info("delete folder-name " + folderName);
        getLdtp().mouseRightClick(folderName.getName());
        onContextMenuPerform("Delete");
        getLdtp().waitTime(2);
        alertConfirmation("Delete Folder", areYouSure);
    }

    /**
     * Delete a folder
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void deleteFolder(File folderName) throws Exception
    {
        deleteFolder(folderName, true);
    }

    /**
     * Right click Cut and Paste which will act like a move operation
     * 
     * @throws Exception
     */
    public File moveContent(File sourceFolder, File destinationLocation) throws Exception
    {
        logger.info("move source folder " + sourceFolder.getAbsolutePath() + " destination location " + destinationLocation.getAbsolutePath());
        cut(sourceFolder);
        openFolder(destinationLocation);
        paste();
        File newMovedFolder = new File(destinationLocation, sourceFolder.getName());
        LdtpUtils.waitUntilFileExistsOnDisk(newMovedFolder);
        return newMovedFolder;
    }

    /**
     * Right click copy and Paste which will act like a move operation
     * 
     * @throws Exception
     */
    public File copyContent(File sourceFolder, File destinationLocation) throws Exception
    {
        logger.info("copy source  " + sourceFolder.getAbsolutePath() + " destination location " + destinationLocation.getAbsolutePath());
        copy(sourceFolder);
        openFolder(destinationLocation);
        paste();
        File newCopyContent = new File(destinationLocation, sourceFolder.getName());
        LdtpUtils.waitUntilFileExistsOnDisk(newCopyContent);
        return newCopyContent;
    }

    /**
     * Just go back in Windows Explorer based on <folder>
     * On Windows 8.1 you don't need to pass folder name
     * 
     * @param folder
     */
    public void goBack(String folder)
    {
        if (LdtpUtils.isWin81() || (LdtpUtils.isWin10()))
        {
            getLdtp().generateKeyEvent("<alt><left>");
        }
        else
        {
            folder = folder.toLowerCase();
            getLdtp().click("Back to " + folder);
        }
        if(LdtpUtils.isWin10())
        {
        	focus("File Explorer");
        }
        else
        {
        	focus(folder);
        }
    }

    /**
     * Going back in Windows Explorer
     */
    public void goBack()
    {
        if (LdtpUtils.isWin81())
        {
            logger.info("Going back using send keys ALT+LEFT");
            getLdtp().generateKeyEvent("<alt><left>");
        }
    }

    /**
     * Rename a file <fileName> using <renamedFileName> parameter
     * Is assumed that we are already on the folder path of the <fileName>
     * 
     * @param fileName
     * @param renamedFileName
     */
    public void rename(String fileName, String renamedFileName)
    {
        String oldWindow = getLdtp().getWindowName();
        getLdtp().mouseRightClick(fileName);
        onContextMenuPerform("Rename");
        getLdtp().generateKeyEvent("<ctrl>a");
        getLdtp().generateKeyEvent(renamedFileName);
        getLdtp().generateKeyEvent("<enter>");
        getLdtp().waitTime(1);
        getLdtp().setWindowName(oldWindow);
    }

    /**
     * Rename a file <fileName> using <renamedFileName> parameter
     * Is assumed that we are already on the folder path of the <fileName>
     * 
     * @param fileName
     * @param renamedFileName
     */
    public void rename(File fileName, File renamedFileName)
    {
        rename(fileName.getName(), renamedFileName.getName());
    }

    /**
     * Delete Confirmation dialog options
     */
    private void alertConfirmation(String window, boolean areYouSure) throws LdtpExecutionError
    {
        String oldWindow = getLdtp().getWindowName();
        getLdtp().setWindowName(window);
        if (areYouSure)
        {
            getLdtp().click("Yes");
        }
        else
        {
            getLdtp().click("No");
        }
        getLdtp().waitTillGuiNotExist(window);
        getLdtp().setWindowName(oldWindow);
    }

    /**
     * Open context menu for a specific folderOrFile
     * 
     * @param folderOrFile
     * @return
     */
    private boolean rightClickOn(String folderOrFile)
    {
        try
        {
            getLdtp().mouseRightClick(folderOrFile);
            getLdtp().setWindowName("Context");
            return true;
        }
        catch (Exception e)
        {
            logger.error(String.format("Context [%s] was not found on rightClickOn method: [%s]", folderOrFile, e.getCause()));
            return false;
        }
    }

    /**
     * Perform an action from Context Menu (e.g. Cut, Paste, etc.)
     * Is assumed that Context menu is already opened.
     * 
     * @param action
     */
    private void onContextMenuPerform(String action)
    {
        String oldWindowName = getLdtp().getWindowName();
        getLdtp().setWindowName("Context");
        getLdtp().click(action);
        getLdtp().setWindowName(oldWindowName);
    }

    /**
     * Right click and Perform actions to create Different application
     * 
     * @author sprasanna
     * @param - folderorFile to right click on
     * @param - name of the file
     * @param - file type
     */
    public void rightClickCreate(String folderorFile, String name, type app) throws LdtpExecutionError
    {
        logger.info("right click and create file type " + app.getType());
        rightClickOn("Items View");
        getLdtp().setWindowName("Context");
        getLdtp().click("New");
        getLdtp().setWindowName(folderorFile);
        getLdtp().mouseMove(app.getType());
        getLdtp().click(app.getType());
        getLdtp().waitTime(2);
        getLdtp().generateKeyEvent("<ctrl>a");
        getLdtp().generateKeyEvent("<ctrl>a");
        getLdtp().generateKeyEvent(name);
        getLdtp().generateKeyEvent("<enter>");
    }

    /*
     * Return the temporary file with image captured
     */
    public File getIconImage(File fileOrFolder) throws Exception
    {

        Ldtp app = new Ldtp(fileOrFolder.getParentFile().getName());
        logger.info("Get Icon Image of: " + fileOrFolder.getPath());
        Integer[] a = app.getObjectSize(Files.getNameWithoutExtension(fileOrFolder.getName()));

        String img = app.imageCapture(a[0], a[1], 20, a[3]);
        logger.info("Saved image in tmp location:" + img);
        File actualImage = new File(img);
        return actualImage;
    }

//    /**
//     * Closing all Window Explorer, Notepad forms, etc.
//     */
//    public void closeAllWindowForms()
//    {
//    	
//        logger.info("Closing all Window Forms opened..");
//
//        LdtpUtils.killAllApplicationsByExeName("notepad.exe");
//        LdtpUtils.killAllApplicationsByExeName("notepad++.exe");
//        LdtpUtils.killAllApplicationsByExeName(VersionDetails.EXCEL.getExeName());
//        LdtpUtils.killAllApplicationsByExeName(VersionDetails.WORD.getExeName());
//        LdtpUtils.killAllApplicationsByExeName(VersionDetails.POWERPOINT.getExeName());
//        /*
//         * need to loop over all objects due to dynamic naming conventions
//         */
////        if(!LdtpUtils.isWin81())
////        {
////        Integer errorCount = 0;
////        while (getOpenedWindows().iterator().hasNext() || errorCount >= 10)
////        {
////            String window = (String) getOpenedWindows().iterator().next();
////            logger.info("Try to close Window: " + window);
////            Ldtp tmpWin = new Ldtp(window);
////            tmpWin.waitTime(1);
////            try
////            {
////                tmpWin.click("Close");
////            }
////            catch (LdtpExecutionError e)
////            {
////                errorCount += 1;
////                logger.error("Error #" + errorCount + " thrown on close window: " + window, e);
////            }
////        }
////        }
////        // this will only close windows explorer for 8.1
////        else
////        {
////        	LdtpUtils.executeOnWin("taskkill /f /im explorer.exe && start explorer");
////        }
//        LdtpUtils.executeOnWin("start /B /NORMAL taskkill /f /im explorer.exe");
// 
//        try
//        {
//            LdtpUtils.runProcess(new String[]{"cmd", "/C", "start", "/B", "/NORMAL", "explorer.exe"});
//        }
//        catch (Exception e)
//        {
//            logger.error("Cannot Restart Explorer" + e.getMessage());
//            e.printStackTrace();
//        }
//
//        logger.info("All Window Forms are now closed! ");
//    }

    /**
     * Closing all Window Explorer, Notepad forms, etc.
     */
    public void closeAllWindowForms()
    {
        logger.info("Closing all Window Forms opened..");

        LdtpUtils.killAllApplicationsByExeName("notepad.exe");
        LdtpUtils.killAllApplicationsByExeName("notepad++.exe");
        LdtpUtils.killAllApplicationsByExeName(VersionDetails.EXCEL.getExeName());
        LdtpUtils.killAllApplicationsByExeName(VersionDetails.WORD.getExeName());
        LdtpUtils.killAllApplicationsByExeName(VersionDetails.POWERPOINT.getExeName());
        /*
         * need to loop over all objects due to dynamic naming conventions
         */
        Integer errorCount = 0;
        while (getOpenedWindows().iterator().hasNext() || errorCount >= 10)
        {
            String window = (String) getOpenedWindows().iterator().next();
            logger.info("Try to close Window: " + window);
            Ldtp tmpWin = new Ldtp(window);
            tmpWin.waitTime(1);
            try
            {
                tmpWin.click("Close");
            }
            catch (LdtpExecutionError e)
            {
                errorCount += 1;
                logger.error("Error #" + errorCount + " thrown on close window: " + window, e);
            }
        }
        logger.info("All Window Forms are now closed! ");
    }
    /**
     * @return ArrayList of all frmWindows opened
     */
    private ArrayList<String> getOpenedWindows()
    {
        String[] windowsList = getLdtp().getWindowList();
        ArrayList<String> arrWindows = new ArrayList<String>();
        for (String window : windowsList)
        {
            if (window.startsWith("frm") && !window.contains("Eclipse") && !window.contains("Bamboo") && !window.toLowerCase().contains("git")
                    && !window.contains("Mozilla") && !window.contains("LDTP") &&!window.contains("Command"))
            {
                try
                {
                if(!LdtpUtils.isWin81())
               	{
                	Ldtp info = new Ldtp(window);
                    if (!LdtpUtils.isApplicationObject(info))
                    {
                        arrWindows.add(window);
                    }
               	}
                else
                	{
                		arrWindows.add(window);
                		
                	}
                }
                catch (Exception e)
                {
                }
            }
        }
        return arrWindows;
    }

    /**
     * CUT operation
     * 
     * @param fileNameOrFolder
     */
    public void cut(File fileNameOrFolder)
    {
        getLdtp().mouseRightClick(fileNameOrFolder.getName());
        onContextMenuPerform("Cut");
    }

    /**
     * COPY operation
     * 
     * @param fileNameOrFolder
     */
    public void copy(File fileNameOrFolder)
    {
        getLdtp().mouseRightClick(fileNameOrFolder.getName());
        onContextMenuPerform("Copy");
    }

    /**
     * PASTE operation
     * 
     * @param fileNameOrFolder
     */
    public void paste()
    {
        getLdtp().mouseRightClick("lstItemsView");
        onContextMenuPerform("Paste");
    }

    /**
     * jump to a specific folder.
     * If that folder is ALREADY open, it will auto-focus.
     */
    public void jumpToLocation(File location) throws IOException
    {
        logger.info("Inside the jump to location method");
        Desktop.getDesktop().open(location); 
        LdtpUtils.waitToLoopTime(1);
        focus(location.getName());
        getLdtp().grabFocus(location.getName());
        getLdtp().waitTillGuiExist();
        maximize();
        logger.info("finished  the jump to location method");
    }
    
    /**
     * DELETE operation
     * 
     * @param fileNameOrFolder
     */
    public void delete(String objectName, boolean confirmationOption){
        getLdtp().mouseRightClick(objectName);
        onContextMenuPerform("Delete");
            getLdtp().waitTime(2);
            alertConfirmation("Delete F*", confirmationOption);
    }
}
