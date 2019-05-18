import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncVideo extends Thread {
    private static String UPLOAD_URL = "https://medialibraryweb.000webhostapp.com/upload.php";
    private ArrayList<Video> videos;
    private ArrayList<String> videosLastUpdated;
    private ArrayList<String> videosOnDirectory;
    private ArrayList<String> videosOnServer;
    private ServerConnection dbConnection;
    private boolean localFileChanged = false;
    private boolean syncSet = false;

    public SyncVideo(ArrayList<Video> videos, ArrayList<String> videosLastUpdated, boolean localFileChanged, boolean syncSet) {
        this.videos = videos;
        this.localFileChanged = localFileChanged;
        this.syncSet = syncSet;
        this.videosOnDirectory = new ArrayList<>();
        for (Video video : videos) {
            videosOnDirectory.add(video.getName());
        }
        this.videosLastUpdated = videosLastUpdated;
        this.dbConnection = new ServerConnection();

        try {
            this.videosOnServer = dbConnection.getItemNameInServer(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (localFileChanged) {
            newfileCheckLocal();
            deleteCheckLocal();
        }
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(videosLastUpdated);
        temp.removeAll(videosOnServer);
        if (temp.size() != 0) {
            deleteCheckServer(temp);
        }
        uploadPendingFiles();
    }

    public void newfileCheckLocal() {
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(videosOnDirectory);
        temp.removeAll(videosLastUpdated);
        try {
            //FileWriter writer = new FileWriter("VideosToBeUploaded.conf");
            for (String video : temp) {
                int index = videosOnDirectory.indexOf(video);
                Video videoToUpload = this.videos.get(index);
                try {
                    int id = dbConnection.uploadVideo(videoToUpload);
                    System.out.println(id);
                    this.uploadFile(UPLOAD_URL, URLFormater(videoToUpload.getPath()), 0, id);
                } catch (Exception x) {
                    x.printStackTrace();
                    // writer.write(video+"\n");
                }
            }
            //writer.close();
        } catch (Exception x) {
        }
    }

    public void deleteCheckLocal() {
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(videosLastUpdated);
        temp.removeAll(videosOnDirectory);
        try {
            FileWriter writer = new FileWriter("VideosToBeDeleted.conf");
            for (String video : temp) {
                try {
                    this.dbConnection.deleteItem(0, video);
                } catch (Exception x) {
                    writer.write(video + "\n");
                }
            }
            writer.close();
        } catch (Exception x) {
        }
    }

    public void deleteCheckServer(ArrayList<String> temp) {
        for (String video : temp) {
            int index = videosOnDirectory.indexOf(video);
            try {
                this.videos.get(index).deleteVideo();
            } catch (Exception x) {
            }
        }
    }

    public int uploadFile(String serverURL, String filePath, int mod, int id) throws IOException {
        String strMod = "";
        switch (mod) {
            case 0:
                strMod = "Videos";
                break;
            case 1:
                strMod = "PDFs";
                break;
            case 2:
                strMod = "Movies";
                break;
        }
        StringBody mode = new StringBody(strMod, ContentType.MULTIPART_FORM_DATA);
        StringBody strId = new StringBody(Integer.toString(id), ContentType.MULTIPART_FORM_DATA);
        HttpClient client = HttpClientBuilder.create().build();
        File file = new File(filePath);
        HttpPost post = new HttpPost(serverURL);
        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
//
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        builder.addPart("mode", mode);
        builder.addPart("id", strId);
        HttpEntity entity = builder.build();
//
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        return 0;
    }


    public synchronized void uploadPendingFiles(){
        HashMap<Integer,String> missedSongs = this.dbConnection.getMissedFiles("movies");
        for (Map.Entry<Integer, String> item : missedSongs.entrySet()) {
            Integer id = item.getKey();
            String value = item.getValue();
            try{
                this.uploadFile(UPLOAD_URL, URLFormater(this.videos.get(this.videosOnDirectory.indexOf(value)).getPath()), 1, id);
            }catch (Exception e){}
        }
    }
    public String URLFormater(String url) {
        return url.replace('\\', '/');
    }
}
