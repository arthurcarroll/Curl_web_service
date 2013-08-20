package com.tireintelligence.rest.example.service.impl;

import com.tireintelligence.rest.example.data.CurlResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author acarroll
 */
public class CurlClientImpl {

    //final static Charset ENCODING = StandardCharsets.UTF_8;
    public CurlResponse runCurlCommand(String commandToRun, boolean base64EncodeResponseBody) {

        Logger.getLogger(CurlClientImpl.class.getName()).log(Level.INFO, MessageFormat.format("commnad: {0}", commandToRun));

        long time = System.currentTimeMillis();
        String fileName = "/tmp/" + "runFile" + time + ".sh";

        String outFileName = "/tmp/" + "outFile" + time + ".out";
        String headerFileName = "/tmp/" + "headerFile" + time + ".out";

        writeTextFile(fileName, commandToRun, outFileName, headerFileName);
        String responseString = "";

        try {
            Process p;
            String command = "sh " + fileName;
            p = Runtime.getRuntime().exec(command);
            p.waitFor();

            List<String> out = readSmallTextFile(outFileName);

            if (out != null) {
                for (String nextLine : out) {
                    responseString += nextLine;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CurlClientImpl.class.getName()).log(Level.ERROR, null, ex);
        }

        Map<String, String> headers = getHeaders(headerFileName);

        CurlResponse curlResponse = new CurlResponse();

        if (base64EncodeResponseBody) {
            curlResponse.responseBody = new String(Base64.encodeBase64(responseString.getBytes()));
        } else {
            curlResponse.responseBody = responseString;
        }
        curlResponse.responsehHeaders = headers;

        removeOldFiles(fileName, outFileName, headerFileName);
        return curlResponse;

    }

    private void removeOldFiles(String inFile, String outFile, String headerFile) {
        try {
            Process p;
            String command = "rm -rf " + inFile + " " + outFile + " " + headerFile;
            Logger.getLogger(CurlClientImpl.class.getName()).log(Level.INFO, MessageFormat.format("Shell command: {0}", command));
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
        } catch (Exception ex) {
            Logger.getLogger(CurlClientImpl.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    private void writeTextFile(String aFileName, String command, String outFile, String headerFile) {
        BufferedWriter writer = null;
        try {
            File file = new File(aFileName);

            FileWriter fw = new FileWriter(file.getAbsoluteFile());

            writer = new BufferedWriter(fw);
            writer.write("curl " + command + " --dump-header " + headerFile + " > " + outFile);
            writer.newLine();
        } catch (Exception ex) {
            Logger.getLogger(CurlClientImpl.class.getName()).log(Level.ERROR, null, ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CurlClientImpl.class.getName()).log(Level.ERROR, null, ex);
            }
        }
    }

    private List<String> readSmallTextFile(String aFileName) {
        BufferedReader br = null;

        List<String> lines = new ArrayList<String>();
        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(aFileName));

            while ((sCurrentLine = br.readLine()) != null) {
                lines.add(sCurrentLine);
            }

            return lines;

        } catch (IOException ex) {
            Logger.getLogger(CurlClientImpl.class.getName()).log(Level.ERROR, null, ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CurlClientImpl.class.getName()).log(Level.ERROR, null, ex);
            }
        }
        return null;
    }

    Map<String, String> getHeaders(String headerFile) {
        try {
            List<String> lines = readSmallTextFile(headerFile);
            if (lines != null) {
                Map<String, String> newMap = new HashMap<String, String>();

                for (String s : lines) {

                    String[] lineSplit = s.split(":");
                    if (lineSplit.length == 2) {
                        newMap.put(lineSplit[0], lineSplit[1]);
                    }
                }

                return newMap;
            }
        } catch (Exception ex) {
            Logger.getLogger(CurlClientImpl.class.getName()).log(Level.ERROR, null, ex);
        }
        return null;
    }
}
