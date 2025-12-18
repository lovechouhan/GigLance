package com.minor.freelancing.Services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ImageServices {

    @Autowired
    private Cloudinary cloudinary;

        
        public Map<String, Object> uploadProposalDocument(MultipartFile file) throws Exception {
        System.out.println("FILE NULL? " + (file == null));
        System.out.println("FILE NAME: " + file.getOriginalFilename());
        System.out.println("FILE SIZE: " + file.getSize());


            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",      // IMPORTANT for PDF/DOC
                            "folder", "gigLance/proposals/"
                    )
            );
        }

    public String uploadFile(MultipartFile profileImage) {
        try {
            Map uploadResult = cloudinary.uploader().upload(profileImage.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "gigLance/profiles/"
                    ));
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
