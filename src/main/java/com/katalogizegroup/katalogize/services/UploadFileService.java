package com.katalogizegroup.katalogize.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import graphql.GraphQLException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

//MongoDB dont have @GeneratedValue for ids so a custom service needs to be created
@Service
public class UploadFileService {

    @Value("${gcp.config.file}")
    private String gcpConfigFile;

    @Value("${gcp.config.project.id}")
    private String gcpProjectId;

    @Value("${gcp.config.bucket.id}")
    private String gcpBucketId;

    @Value("${gcp.config.dir.name}")
    private String gcpDirectoryName;

    public String uploadFile(String folderName, String imageCategory, String fileUpload) {
        try {
            //File conversion
            String fileType = fileUpload.split(";")[0].split(":")[1];
            if (!fileType.equals("image/png") && !fileType.equals("image/jpeg") && !fileType.equals("image/jpg")) throw new GraphQLException("Unsupported file type");
            String base64Image = fileUpload.split(",")[1];
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
            File outputFile = new File("image.png");

            //File verification
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            bis.close();
            if (image == null) throw new GraphQLException("Unsupported file");
            //ImageIO.write(image, "png", outputFile);

            //Access GCS
            Bucket bucket = getGCSBucket();

            //Upload to GCS
            String id = new ObjectId().toString();
            Blob blob = bucket.create(gcpDirectoryName + "/" + folderName + "/" + imageCategory + "-" + id + ".png", imageBytes, Files.probeContentType(outputFile.toPath()), Bucket.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
            if (blob == null) throw new GraphQLException("An error occurred while storing data to GCS");
            String name = blob.getName();
            return name;
        } catch (Exception e) {
            throw new GraphQLException("An error happened while uploading the image");
        }
    }

    public Boolean deleteFile(String fileName) {
        try {
            Storage storage = getGCSStorage();
            return storage.delete(gcpBucketId, fileName);
        } catch (Exception e) {
            throw new GraphQLException("An error happened while uploading the image");
        }
    }

    private Storage getGCSStorage() {
        try {
            InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
            Storage storage = options.getService();
            return storage;
        } catch (Exception e) {
            throw  new GraphQLException("Error while accessing storage");
        }
    }
    private Bucket getGCSBucket() {
        try {
            Storage storage = getGCSStorage();
            Bucket bucket = storage.get(gcpBucketId, Storage.BucketGetOption.fields());
            return bucket;
        } catch (Exception e) {
            throw  new GraphQLException("Error while accessing bucket");
        }
    }
}
