package web.app.Controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.app.Identification.MediaIdentification;
import web.app.Utility.DefaultServerComm;
import web.app.Utility.ServerCommunication.HomeServerComm;

import javax.net.ssl.SSLSocket;

@RestController
@RequestMapping("/api")
public class HomeController {



    @GetMapping("/mediaimages")
    public String [] media() {
        if (MediaIdentification.GetAllFileNames() != null) {
            return MediaIdentification.GetAllFileNames();
        }

        SSLSocket socket = DefaultServerComm.Connect();
        if (socket == null) {
            return new String[0];
        }

        HomeServerComm.SendStageChoice(socket, "Images");

        int imageCount = HomeServerComm.GetImageNumber(socket);

        MediaIdentification.Init(imageCount);
        HomeServerComm.GetAllImages(socket, imageCount);

        String[] imageUrls = MediaIdentification.GetAllFileNames();
        DefaultServerComm.SocketClose(socket);
        return imageUrls != null ? imageUrls : new String[0];
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logOut() {
        //
        return ResponseEntity.noContent().build();
    }
    
}
