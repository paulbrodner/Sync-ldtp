/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.os.mac.app;

import java.io.File;

import org.alfresco.exceptions.WindowNotOpenedException;
import org.alfresco.os.mac.utils.AlertDialog;
import org.alfresco.os.mac.utils.KeyboardShortcut;
import org.alfresco.utilities.LdtpUtils;

/**
 * This class will handle Finder based action over Files (CRUD) and Folder (CRUD).
 * 
 * @task QA-1107
 * @author <a href="mailto:paulbrodner@gmail.com">Paul Brodner</a>
 */
public class FinderExplorer extends KeyboardShortcut
{
    /**
     * Initialize the FinderExplorer window for Mac
     * Opening first the Documents folder of the current user
     */
    public FinderExplorer()
    {
        this(LdtpUtils.getDocumentsFolder().getPath());
    }

    /**
     * Initialize a new Finder Windows starting on <startUpFolder>
     * 
     * @param startUpFolder
     */
    public FinderExplorer(String startUpFolder)
    {
        setApplicationName("Finder");
        // set the root path of the Finder Window to the current user Documents folder
        setApplicationPath(startUpFolder);
        // each finder has the window name set to the current folder name
        setWaitWindow(new File(startUpFolder).getName());
    }

    /**
     * use the AlertDialog class
     * 
     * @return AlertDialog
     */
    public AlertDialog alert()
    {
        return new AlertDialog();
    }

    /**
     * Enumerate possible Layouts that we can define before we start using the Finder explorer.
     * Use LIST as default
     * 
     * @author Paul Brodner
     */
    public enum LayoutView
    {
        ICONS("0"), LIST("1"), GROUP("2"), COVER_FLOW("3");
        private String position;

        private LayoutView(String position)
        {
            this.position = position;
        }

        public String getIdentifiler()
        {
            return "rbtn" + position;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.utilities.ApplicationAbstract#openApplication()
     * Overrite the openApplication in order to also set the default view of the folder
     */
    public FinderExplorer openApplication()
    {
        super.openApplication();
        setViewLayout(LayoutView.LIST);
        return this;
    }

    @Override
    public void exitApplication()
    {
        minimize();
        killPython();
    }

    /**
     * Define a default Layout on the Finder Window
     * 
     * @param layoutView
     */
    public void setViewLayout(LayoutView layoutView)
    {
        getLdtp().click(layoutView.getIdentifiler());
    }

    /**
     * This will open a folder based on <folderPath> parameter passed
     * 
     * @param folderPath
     */
    public void openFolder(File folderPath)
    {
        focus();
        getLdtp().generateKeyEvent("<shift><command>g");
        getLdtp().generateKeyEvent(folderPath.getPath());
        // getLdtp().click("btnGo");
        getLdtp().generateKeyEvent("<enter>");
        LdtpUtils.waitToLoopTime(1);
    }

    /**
     * Create a new Folder based on MAC shortcut keys
     * 
     * @param folderName
     */
    public void createFolder(File folderPath)
    {
        focus();
        openFolder(folderPath.getParentFile());
        getLdtp().generateKeyEvent("<shift><command>n");
        getLdtp().generateKeyEvent(folderPath.getName());
        getLdtp().generateKeyEvent("<enter>");
    }

    /**
     * Delete a folder based on MAC shortcut keys
     * First we navigate to target folder, go up one level and delete it using cmd+bksp
     * 
     * @param folderPath
     */
    public void deleteFolder(File folderPath)
    {
        openFolder(folderPath);
        goToEnclosingFolder();
        cmdDelete();
    }

    /**
     * Move a folder from <source> to <destination>
     * 
     * @param source
     * @param destination
     */
    public void moveFolder(File source, File destination)
    {
        openFolder(source);
        goToEnclosingFolder();
        cmdCopy();
        openFolder(destination);
        cmdMove();
    }

    public void copyFolder(File source, File destination)
    {
        openFolder(source);
        goToEnclosingFolder();
        cmdCopy();
        openFolder(destination);
        cmdPaste();
    }

    /**
     * Rename <folder> with <newName>
     * 
     * @param folder
     * @param newName
     */
    public void renameFolder(File folder, String newName)
    {
        openFolder(folder);
        goToEnclosingFolder();
        getLdtp().generateKeyEvent("<enter>");
        getLdtp().generateKeyEvent(newName);
        getLdtp().generateKeyEvent("<enter>");
    }

    /**
     * Restore any deleted Folder
     * 
     * @param filename
     */
    public void restoreDeletedFolder(File folder)
    {
        File deletedFolder = new File(LdtpUtils.getTrashFolderLocation(), folder.getName());
        deleteFolder(deletedFolder); // on MAC: cmd+bkspace will do the trick and restore the file
    }

    /**
     * Open a file using default Application
     * 
     * @param file
     */
    public void openFile(File file)
    {
        selectFile(file);
        goActivate();
    }

    /**
     * Select a file in Finder
     * 
     * @param file
     */
    public void selectFile(File file)
    {
        openFolder(file);
    }

    /**
     * Delete a file
     * 
     * @param file
     */
    public void deleteFile(File file)
    {
        selectFile(file);
        cmdDelete();
    }

    /**
     * Move a file from <source> to <destination>
     * 
     * @param source
     * @param destination
     */
    public void moveFile(File source, File destinationFolder)
    {
        selectFile(source);
        cmdCopy();
        openFolder(destinationFolder);
        cmdMove();
    }

    /**
     * Copy a file from <source> to <destination>
     * 
     * @param source
     * @param destinationFolder
     */
    public void copyFile(File source, File destinationFolder)
    {
        selectFile(source);
        cmdCopy();
        openFolder(destinationFolder);
        cmdPaste();
    }

    /**
     * Rename <folder> with <newName>
     * 
     * @param folder
     * @param newName
     */
    public void renameFile(File file, String newName)
    {
        openFolder(file);
        getLdtp().generateKeyEvent("<enter>");
        cmdAll();
        getLdtp().generateKeyEvent(newName);
        getLdtp().generateKeyEvent("<enter>");
    }

    /**
     * Restore any deleted files
     * 
     * @param filename
     */
    public void restoreDeletedFile(File filename)
    {
        File deletedFile = new File(LdtpUtils.getTrashFolderLocation(), filename.getName());
        deleteFile(deletedFile); // on MAC: cmd+bkspace will do the trick and restore the file
    }

    /**
     * Empty the trash file
     * Command-Shift-Option-Delete Empty Trash without confirmation dialog
     * 
     * @throws Exception
     */
    public void emptyTrash() throws Exception
    {
        String trashWin = "Trash";
        openFolder(LdtpUtils.getTrashFolderLocation());
        if (isWindowOpened(trashWin))
        {
            cmdAll();
            // Command-Shift-Option-Delete Empty Trash without confirmation dialog
            getLdtp().generateKeyEvent("<command><shift><alt><bksp>");
        }
        else
        {
            throw new WindowNotOpenedException("Could not open Trash folder");
        }
    }
}
