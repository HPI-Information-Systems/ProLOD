/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.service;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.algorithms.dataimport.ImportJob;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.shared.config.dataimport.FileType;

@Singleton
public class ImportServlet extends UploadAction {

  @Inject
  private Log myLogger;
  @Inject
  private Provider<ImportJob> jobSource;
  @Inject
  private JobManager manager;

  @Inject
  void setMaxSize(@Named("upload.maxupload") long size) {
    super.maxSize = size;
  }

  @Inject
  void setUploadDelay(@Named("upload.slowuploads") int delay) {
    super.uploadDelay = delay;
  }

  @Inject
  void setJobManager(JobManager manager) {
    this.manager = manager;
  }

  private static final long serialVersionUID = 1L;

  Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
  /**
   * Maintain a list with received files and their content types.
   */
  Hashtable<String, File> receivedFiles = new Hashtable<String, File>();

  @Override
  public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles)
      throws UploadActionException {
    Enumeration<?> enu = request.getAttributeNames();
    Object x;
    while (enu.hasMoreElements()) {
      x = enu.nextElement();

      myLogger.info("### " + x);
    }
    String schema = null;
    String label = null;
    FileType fileType = null;
    String fileName = null;
    for (FileItem fileItem : sessionFiles) {
      try {
        final String name = fileItem.getFieldName();

        if ("schema".equalsIgnoreCase(name)) {
          schema = fileItem.getString();
        } else if ("label".equalsIgnoreCase(name)) {
          label = fileItem.getString();
        } else if ("fileType".equalsIgnoreCase(name)) {
          fileType = FileType.valueOf(fileItem.getString());
        }
        if (name.toUpperCase().startsWith("GWTU-")) {

          final long size = fileItem.getSize();
          // store them somewhere
          myLogger.info("filesize: " + size);

          File file = File.createTempFile("upload-", ".bin");
          fileItem.write(file);

          fileName = file.getCanonicalPath();
        }
      } catch (IOException e) {
        myLogger.error("Unable to handle uploaded file!", e);
        throw new UploadActionException(e);
      } catch (Exception e) {
        myLogger.error("Unable to handle uploaded file!", e);
        throw new UploadActionException(e);
      }
    }
    String missingParameter = null;
    if (schema == null) {
      missingParameter = "schema name";
    }
    if (label == null) {
      missingParameter = "label";
    }
    if (fileName == null) {
      missingParameter = "file";
    }
    if (fileType == null) {
      missingParameter = "file type";
    }

    if (missingParameter != null) {
      if (fileName != null) {
        // delete tmp file
        File f = new File(fileName);
        if (!f.delete()) {
          myLogger.error("Unable to delete temporary file for \"" + schema + "\" (" + fileName
              + ")");
        } else {
          myLogger.debug("Temporary file for \"" + schema + "\" (" + fileName
              + ") successfully deleted!");
        }
      }

      throw new IllegalStateException("Missing parameter " + missingParameter + "!");
    }
    myLogger.info("schema: " + schema);
    myLogger.info("label: " + label);
    myLogger.info("file name: " + fileName);
    myLogger.info("file type: " + (fileType != null ? fileType.getExtension() : null));
    final long fileSize = new File(fileName).length();
    myLogger.info("file size (KB): " + (fileSize / 1024.0));
    // myLogger.info("file2: " + fileItem.getName());
    // myLogger.info("file3: " + fileItem.getString());

    ImportJob importJob = this.jobSource.get();
    importJob.init(schema, label, fileName, fileType);

    final long id = this.manager.executeJob(importJob);
    final String result = Long.toString(id);

    myLogger.info("job id: " + result);
    myLogger.info("===");

    return result;
  }

  // /**
  // * Override executeAction to save the received files in a custom place and
  // * delete this items from session.
  // */
  // @Override
  // public String executeAction(HttpServletRequest request, List<FileItem>
  // sessionFiles)
  // throws UploadActionException {
  // String response = "";
  // int cont = 0;
  // for (FileItem item : sessionFiles) {
  // if (false == item.isFormField()) {
  // cont++;
  // try {
  // // / Create a new file based on the remote file name in the
  // // client
  // // String saveName =
  // // item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+",
  // // "_");
  // // File file =new File("/tmp/" + saveName);
  //
  // // / Create a temporary file placed in /tmp (only works in
  // // unix)
  // // File file = File.createTempFile("upload-", ".bin", new
  // // File("/tmp"));
  //
  // // / Create a temporary file placed in the default system
  // // temp folder
  // File file = File.createTempFile("upload-", ".bin");
  // item.write(file);
  //
  // // / Save a list with the received files
  // receivedFiles.put(item.getFieldName(), file);
  // receivedContentTypes.put(item.getFieldName(), item.getContentType());
  //
  // // / Compose a xml message with the full file information
  // // which can be parsed in client side
  // response += "<file-" + cont + "-field>" + item.getFieldName() + "</file-"
  // + cont + "-field>\n";
  // response += "<file-" + cont + "-name>" + item.getName() + "</file-" +
  // cont
  // + "-name>\n";
  // response += "<file-" + cont + "-size>" + item.getSize() + "</file-" +
  // cont
  // + "-size>\n";
  // response += "<file-" + cont + "-type>" + item.getContentType() +
  // "</file-"
  // + cont + "type>\n";
  // } catch (Exception e) {
  // throw new UploadActionException(e);
  // }
  // }
  // }
  //
  // // / Remove files from session because we have a copy of them
  // removeSessionFileItems(request);
  //
  // // / Send information of the received files to the client.
  // return "<response>\n" + response + "</response>\n";
  // }
  //
  // /**
  // * Get the content of an uploaded file.
  // */
  // @Override
  // public void getUploadedFile(HttpServletRequest request,
  // HttpServletResponse response)
  // throws IOException {
  // String fieldName = request.getParameter(PARAM_SHOW);
  // File f = receivedFiles.get(fieldName);
  // if (f != null) {
  // response.setContentType(receivedContentTypes.get(fieldName));
  // FileInputStream is = new FileInputStream(f);
  // copyFromInputStreamToOutputStream(is, response.getOutputStream());
  // } else {
  // renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
  // }
  // }
  //
  // /**
  // * Remove a file when the user sends a delete request.
  // */
  // @Override
  // public void removeItem(HttpServletRequest request, String fieldName)
  // throws UploadActionException {
  // File file = receivedFiles.get(fieldName);
  // receivedFiles.remove(fieldName);
  // receivedContentTypes.remove(fieldName);
  // if (file != null) {
  // file.delete();
  // }
  // }
}
