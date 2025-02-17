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
import java.io.IOException;

import org.alfresco.os.mac.Editor;
import org.alfresco.os.mac.utils.AppleScript;
import org.alfresco.utilities.LdtpUtils;
import org.apache.log4j.Logger;

import com.cobra.ldtp.LdtpExecutionError;

/**
 * This class will handle operations related to TextEditor application on MAC operating system.
 * 
 */
public class TextEdit extends Editor
{
	private static Logger logger = Logger.getLogger(TextEdit.class);
	/**
     * @param version
     * @param application 'Microsoft Word.app' or 'Microsoft Outlook.app'
     * @return Ldtp
     * @throws LdtpExecutionError
     * @throws IOException
     */
    public TextEdit()
    {
        setApplicationPath("/Applications/TextEdit.app");
        setApplicationName("TextEdit");
        setWaitWindow("Untitled");// appTextEdit
        setFileName("Untitled");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.os.mac.EditorAbstract#save(java.lang.String)
     */
    public void save(File location)
    {
        logger.info("Save document to: " + location);
        // paul.brodner: "/" char cannot be added to TextEditor via LDTP
        // we used an alternative: with AppleScript
        goToLocation(location);
        waitForFileOnDisk(location);
    }

    /**
     * Helper for saving file using File object
     * 
     * @param file
     * @throws Exception 
     */
//    public void save(File file) throws Exception
//    {
//        save(file.getPath());
//    }

    /**
     * Using the AppleScript commands, we can activate and add proper input values
     * This will input "/" - so Go to Folder dialog will appear
     * will add the location string and save the file
     * 
     * @param folder
     * @throws Exception 
     */
    private void goToLocation(File location) 
    {
    	System.out.println(location);
        AppleScript appleScript = getAppleScript();
        appleScript.clean();
        appleScript.addCommandScript("tell app \"TextEdit\" to activate");
        appleScript.addCommandScript("delay 2");
        appleScript.addCommandScript("tell application \"System Events\"");
        appleScript.addCommandScript("keystroke \"s\" using {command down}");
        appleScript.addCommandScript("delay 1.5");
        appleScript.addCommandScript("keystroke \"a\" using {command down}");
        appleScript.addCommandScript("delay 0.5");
        appleScript.addCommandScript("keystroke \"a\" using {command down}");
        appleScript.addCommandScript("delay 1");
        appleScript.addCommandScript("keystroke \"" + location.getParentFile().getPath() + "\"");
        appleScript.addCommandScript("delay 1");
        appleScript.addCommandScript("keystroke return");
        appleScript.addCommandScript("delay 2");
        appleScript.addCommandScript("keystroke \"" + location.getName() + "\"");
        appleScript.addCommandScript("delay 2");
        appleScript.addCommandScript("keystroke return");
        appleScript.addCommandScript("end tell");
        appleScript.run();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.os.mac.EditorAbstract#saveAndClose()
     */
    public void saveAndClose()
    {
        logger.info("Save and close current file.");
        getLdtp().generateKeyEvent("<command>s");
        close(getFileName());
    }

    public void saveAs(File file) 
    {
        logger.info("Save file As: " + file.getPath());
        getLdtp().generateKeyEvent("<command><shift>s");
        save(file);
    }

    /**
     * Create a new File locally
     * 
     * @param file
     * @throws Exception 
     */
    public void createFile(File file) throws Exception
    {
        logger.info("Create file: " + file.getPath());
        openApplication();
        edit("test file");
        saveAs(file);
        exitApplication();
       Thread.sleep(2000);
    }

    /**
     * method to open a file in textedit
     */
    public void openFile(File file) throws Exception
    {
        logger.info("Open file: " + file.getPath());
        LdtpUtils.logInfo("Opening Application: " + getApplicationPath());
        runProcess(new String[] { "open", getApplicationPath() });
        LdtpUtils.waitToLoopTime(2);
        openFromFileMenu(file);
        Thread.sleep(1000);
    }
}
