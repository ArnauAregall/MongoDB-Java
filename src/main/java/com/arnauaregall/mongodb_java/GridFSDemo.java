package com.arnauaregall.mongodb_java;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Demo class that stores a file from your system in MongoDB with the GridFS API
 * and if all went well, it takes out a copy from the database.
 * This code was written by following 10gen's MongoDB for Java Developers course (M101J).
 *
 * @author ArnauAregall
 */
public class GridFSDemo {
    public GridFSDemo() {
        MongoClient client = null;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        Scanner scanner =  null;
        String fileName = "";
        String fileDescription = "";
        String fileTag = null;

        scanner = new Scanner(System.in);
        while (fileName.equals("")) {
            System.out.println("Please, write the system path of the file you want to store in MongoDB (can be more than 16MB, yay!)." +
                    "\nExample: mySong.mp3");
            fileName = scanner.nextLine();
        }

        System.out.println("Do you want to add a description? :" +
                "\nExample: My favourite song");
        fileDescription = scanner.nextLine();

        System.out.println("Now you might want to add a tag for your file." +
                "\nExample: music. You should add at least one.");
        List<String> tags = this.askForTags(scanner);

        try {
            client = new MongoClient();
            DB db = client.getDB("gridfs_test");

            GridFS songs = new GridFS(db, "songs");
            songs.remove(new BasicDBObject());

            // let's read an MP3 file through our input stream
            inputStream = new FileInputStream(fileName);

            GridFSInputFile songFile = songs.createFile(inputStream, fileName);

            // let's associate a basic metadata document to our file:
            BasicDBObject metadata = new BasicDBObject(
                    "file_description", fileDescription)
                    .append("file_owner", System.getProperty("user.name"))
                    .append("file_tags", tags);

            songFile.setMetaData(metadata);
            songFile.save();

            System.out.println("Object ID in our \"songs\" collection : " +
                    songFile.get("_id"));

            System.out.println("Saved file to MongoDB :)");

            System.out.println("Let's check it : \n" + songs.find(new BasicDBObject()));

            System.out.println("Let's read and make a copy of it :");

            String fileCopyName = fileName.substring(
                    fileName.lastIndexOf(System.getProperty("file.separator")) + 1);

            GridFSDBFile gridFSFile = songs.findOne(new BasicDBObject(
                    "filename", fileName));
            outputStream = new FileOutputStream("(Copy from MongoDB) " + fileCopyName);
            gridFSFile.writeTo(outputStream);

            System.out.println("Written file out from MongoDB :)");

        } catch(UnknownHostException e) {
            System.err.println("Ooops, can't connect to MongoDB server : \n" + e);
        } catch(IOException e)  {
            System.err.println("Ooops, something went wrong when reading/writing file : \n" + e);
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
                if (scanner != null)
                    scanner.close();
                if (client != null)
                    client.close();
            } catch (Exception e) {
                System.err.println("Something went really bad : \n" + e);
            }
        }
    }

    private List<String> askForTags(Scanner scanner) {
        List<String> tags = new ArrayList<String>();
        String fileTag = null;
        while (fileTag == null) {

            if (tags.size() > 0) {
                System.out.println("Here are the tags you introduced : ");
                for (String tag : tags) {
                    System.out.println(tag);
                }
                System.out.println("If you want to add another tag, introduce the it's name." +
                        "\nIf you had enough, press return.");
            }

            fileTag = scanner.nextLine();
            if (fileTag.equals("")) {
                if (tags.size() > 0) {
                    break;
                } else {
                    fileTag = null;
                }
            }
            tags.add(fileTag);
            fileTag = null;
        }
        scanner.close();
        return tags;
    }
}
