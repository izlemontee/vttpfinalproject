package izt.spotifyserver.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.repositories.UserSQLRepository;

@Service
public class ImageService {
    @Autowired
    private AmazonS3 s3;

    @Autowired
    private UserSQLRepository userSQLRepository;

    private final String IMAGE_BASE_URL = "https://izlemonteebucket.sgp1.digitaloceanspaces.com/images/%s";

    public String saveImageToS3(InputStream is, String contentType, long length){
        ObjectMetadata metadata = new ObjectMetadata();
        Map<String, String> dataMap = new HashMap<>();

        // just to set metadata
        dataMap.put("name", "fred");

        metadata.setContentType(contentType);
        metadata.setContentLength(length);
        metadata.setUserMetadata(dataMap);

        String id = UUID.randomUUID().toString().substring(0,8);

        // image upload request
        PutObjectRequest putReq = new PutObjectRequest(
            "izlemonteebucket", //bucket name
            "images/%s".formatted(id), // key
            is, metadata); // image input stream and metadata

        // upload to S3
        putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
        // YOU NEED THE LINE ABOVE in order to make it publicly read
        PutObjectResult result = s3.putObject(putReq);
        
        return id; 

    }

    public void updateProfilePicture(MultipartFile file, String username)throws IOException{
        String imageId = saveImageToS3(file.getInputStream(), file.getContentType(), file.getSize());
        String imageUrl = IMAGE_BASE_URL.formatted(imageId);
        long count = userSQLRepository.addImageToUser(imageUrl, username);
        if(count < 1){
            throw new SQLFailedException();
        }

    }


    
}
