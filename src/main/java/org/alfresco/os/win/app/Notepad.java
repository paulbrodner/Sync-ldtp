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

import java.io.File;

import org.alfresco.os.win.Application;
import org.alfresco.utilities.LdtpUtils;
import org.apache.log4j.Logger;

import com.cobra.ldtp.LdtpExecutionError;
import com.google.common.io.Files;

/**
 * This class has all the method involved in using the actions in Notepad application 2013
 * 
 * @author Subashni Prasanna
 * @author Paul Brodner
 */
public class Notepad extends Application
{
	private static Logger logger = Logger.getLogger(Notepad.class);
    String applicationWindowName = "Notepad";

    public Notepad()
    {
        setApplicationName("notepad.exe");
        // set the root path of the Finder Window to the current user Documents folder
        setApplicationPath(new File(LdtpUtils.getSystem32(),getApplicationName()).getPath()); 
       
        // each finder has the window name set to the current folder name
        setWaitWindow("Notepad");
    }

    /**
     * Edit a content in notepad
     */
    public void edit(String data)
    {
        getLdtp().enterString("txt0", data);
    }

    /**
     * Edit a content in notepad
     */
    public void appendData(String data)
    {
        getLdtp().appendText("txt0", data);
    }

    /**
     * Save As notepad in a particular location
     * 
     * @throws Exception
     */
    public void saveAs(File destinationFile) throws Exception
    {
        getLdtp().doubleClick("File");
        getLdtp().click("Save As");
        waitForWindow("Save As");
        LdtpUtils.logInfo("before enter string");
        getLdtp().enterString("txtFilename", destinationFile.getPath());
        LdtpUtils.logInfo("after enter string");
        getLdtp().click("btnSave");
        LdtpUtils.waitToLoopTime(1);
    }

    /**
     * Save As notepad in a particular location
     * by overwriting existing file
     *
     * @throws Exception
     */
    public void saveAsWithOverwrite(File destinationFile) throws Exception
    {
        getLdtp().doubleClick("File");
        getLdtp().click("Save As");
        waitForWindow("Save As");
        getLdtp().enterString("txtFilename", destinationFile.getPath());
        getLdtp().click("btnSave");
        LdtpUtils.waitToLoopTime(1);
        getLdtp().click("Yes");
    }

    /**
     * Close the notepad application after the save
     */
    public void close(File fileName) throws LdtpExecutionError
    {
        focus(fileName);
        getLdtp().doubleClick("File");
        getLdtp().click("Exit");
        setWaitWindow("Notepad");
    }
    
    /**
     * Just close the Notepad application
     * Be aware that first you will need to have a focus on the application
     * use {@link focus} method 
     */
    public void close(){
        getLdtp().click("Close");
        setWaitWindow("Notepad");
    }

    /**
     * Ctrl - S and save the file
     */
    public void save()
    {
        getLdtp().keyPress("<ctrl>S");
        getLdtp().keyRelease("<ctrl>S");
    }

    /**
     * Focus Notepad - bring to top, based on filename parameter
     * 
     * @param partialFileName
     */
    public void focus(File partialFileName)
    {
        String winName = partialFileName.getName();
        String fileName =  Files.getNameWithoutExtension(winName);
        try
        {
            waitForApplicationWindow(fileName, true);
            String fullName = LdtpUtils.getFullWindowList(getLdtp(), fileName);
            getLdtp().activateWindow(fullName);
        }
        catch (Exception e)
        {
            logger.error("Could not find Notepad file:" + winName, e);
        }
    }

    public boolean isFileReadOnly()
    {
        try
        {
            String readOnlyText = getLdtp().getObjectProperty("lbl*This file*", "label");
            if (readOnlyText.contains("This file is set to read-only"))
                return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }
    
    
	/**
	 * Save the file using shortcut keys
	 * @param fileName
	 * @throws Exception
	 */
	public void saveAsWithShortcutKeys(File fileName) throws Exception {
		save();
		waitForWindow("Save As");
	    getLdtp().enterString("txtFilename", fileName.getPath());
	    getLdtp().click("btnSave");
	    LdtpUtils.waitToLoopTime(1);
	}

	/**
	 * Create file on notepad.
	 * @param fileToRename
	 * @throws Exception
	 */
	public void createFile(File fileToRename) throws Exception {
		openApplication();
		edit("data");
		saveAs(fileToRename);
		exitApplication();
		getLdtp().waitTime(2);
	}

    public String getNotepadText()
    {
        String text = getLdtp().getTextValue("txt0");
        return text;
    }
    /**
     * set windows name with the name of file 
     * @throws Exception 
     */
    
    public void setNotepad(String fileName) throws Exception
    {
        waitForApplicationWindow(fileName, true);
    }
}
