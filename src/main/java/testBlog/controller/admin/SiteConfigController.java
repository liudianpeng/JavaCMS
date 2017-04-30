package testBlog.controller.admin;
import org.apache.tomcat.jni.Directory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.annotation.PostConstruct;
import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.ResponseWrapper;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

//import java.io.InputStream;
//import java.io.OutputStream;


@Controller

@RequestMapping(value = "admin/site-config", produces  = MediaType.ALL_VALUE)
public class SiteConfigController {

    private File site_background_images_dir;
    public File user_upload_dir;

    public SiteConfigController() {
        site_background_images_dir = new File(
                System.getProperty("user.dir") +
                        System.getProperty("file.separator") +
                        "siteconfig" +
                        System.getProperty("file.separator") +
                        "backgrounds"
        );
        if (!this.site_background_images_dir.exists()) {
                this.site_background_images_dir.mkdirs();
        }
    }

    public SiteConfigController(String user_name,String content_type) {
        this.user_upload_dir = new File(System.getProperty("user.dir") + System.getProperty("file.separator") +
                "users_files" + System.getProperty("file.separator") + user_name + System.getProperty("file.separator") +
                content_type + System.getProperty("file.separator")
        );
        if (!this.user_upload_dir.exists()) {
            this.user_upload_dir.mkdirs();
        }
    }

    @GetMapping("")
    public String getView(Model model) {
        model.addAttribute("view","admin/site-config/settings");
        return "base-layout";
    }

    @RequestMapping(method = RequestMethod.GET,value = "/content/content_type={content_type}&user={user}&dir={dir}",consumes = {"text/plain"})
    public  void content_images(HttpServletResponse responseMain,@PathVariable("content_type") String content_type,@PathVariable("user") String user,@PathVariable("dir") String dir) {
        String[] response = {};
        switch (content_type) {
            case "images": {response = get_files_list(site_background_images_dir); break;}
            default: {System.out.println("GGG");}
        }
        for (int i=0;i<2;i++) {
            File file = new File(response[i]);

            if (file.getName().equals(".DS_Store")) { continue;}
            try {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
                responseMain.setContentType(mimeType);
                responseMain.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
                responseMain.setContentLength((int)file.length());
                int result = FileCopyUtils.copy(inputStream,responseMain.getOutputStream());
                System.out.println(result);
            } catch (IOException exception) {
                System.out.println(exception);
                //return exception.toString();
            }
        }

        //return "XXX";

    }


    @RequestMapping(method = RequestMethod.POST,value = "/inComingMessage",consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_JPEG_VALUE},produces = MediaType.TEXT_HTML_VALUE)
    @ResponseWrapper
    public @ResponseBody String inComingMessage(@RequestParam("job") String getJobName, @RequestParam("param") String getParamaValue, @RequestPart("files") MultipartFile[] multipartFile, @RequestHeader() HttpHeaders headers) {
        System.out.println(System.getProperties());
        System.out.println(getJobName);
        String status = "No job done,yet!";
        switch (getJobName) {
            case "imageUpload_siteBackground": {status = write_files(multipartFile); break;}
            case "imageUpload_siteBackground_and_change": {status = write_files(multipartFile); break;}
            default: System.out.println("Error");
        }
        return status;
    }

    private String changeSiteBGImage(String paramValue) {
        System.out.println(paramValue);
        return "Hello";
    }

    private String write_files(MultipartFile[] files) {
        String status = "read_write_file_done";
        for (int i = 0; i < files.length; i++) {
            try {
                byte[] fileBytes = files[i].getBytes();
                //status += "   " + Base64.getEncoder().encodeToString(fileBytes);
                File file = new File(site_background_images_dir + System.getProperty("file.separator") + files[i].getOriginalFilename().replaceAll(" ","_").replaceAll("\\(","_").replaceAll("\\)","_"));
                BufferedOutputStream stream  = new BufferedOutputStream(new FileOutputStream(file));
                stream.write(fileBytes);
                stream.close();
            } catch (java.io.IOException exeption) {
                System.out.println(exeption);
                status = exeption.toString();
            }
        }
        get_files_list(this.site_background_images_dir);
        return status;
    }

    private String[] get_files_list(File direcetory_to_read_files_from) {
        String[] file_names = direcetory_to_read_files_from.list();
        for ( int i = 0; i<file_names.length; i++ ) {
            file_names[i] = direcetory_to_read_files_from.getPath() +
                            System.getProperty("file.separator") +
                            file_names[i];
        }
        return file_names;
    }
 }

