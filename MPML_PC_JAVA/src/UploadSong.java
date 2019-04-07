import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;

public class UploadSong extends Thread{
    private static String UPLOAD_URL = "https://medialibraryweb.000webhostapp.com/upload.php";
    private ArrayList<Song> songs;
    private ArrayList<String> songsLastUpdated;
    private ArrayList<String> songsOnDirectory;
    private ArrayList<String> songsOnServer;
    private DBConnection dbConnection;
    private boolean upload;
    public UploadSong(ArrayList<Song> songs, boolean upload){
        this.upload=upload;
        this.songs = songs;
        this.songsOnDirectory = new ArrayList<>();
        for (Song song:songs) {
            songsOnDirectory.add(song.getName());
        }
        this.songsLastUpdated = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Sync.conf"));

            String line = reader.readLine();

            while (line !=""){
                songsLastUpdated.add(line.trim());
                line = reader.readLine();
            }
        }catch (Exception x){}
        this.dbConnection = new DBConnection();

        try {
            this.songsOnServer = dbConnection.getItemNameInServer(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        if(!songsOnDirectory.equals(songsLastUpdated)) {
            newfileCheckLocal();
            deleteCheckLocal();
        }
        deleteCheckServer();
        refreshSyncFile();
    }

    public void newfileCheckLocal(){
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(songsOnDirectory);
        temp.removeAll(songsLastUpdated);
        try{
            FileWriter writer = new FileWriter("SongsToBeUploaded.conf",true);
        for (String song: temp) {
            int index = songsOnDirectory.indexOf(song);
            Song songToUpload = this.songs.get(index);
            try {
                if(this.upload) {
                    int id = dbConnection.uploadSong(songToUpload);
                    this.uploadFile(UPLOAD_URL, URLFormater(songToUpload.getPath()), 0, id);
                }
                else{
                    throw new Exception();
                }
            }catch (Exception x){
                x.printStackTrace();
                writer.write(song+"\\n");
            }
        }
        writer.close();
    }catch (Exception x){}
    }
    public void deleteCheckLocal(){
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(songsLastUpdated);
        temp.removeAll(songsOnDirectory);
        try{
            FileWriter writer = new FileWriter("SongsToBeDeleted.conf",true);
        for (String song: temp) {
            int index = songsOnDirectory.indexOf(song);
            try {
                this.dbConnection.deleteItem(0, song);
            }catch (Exception x){
                writer.write(song+"\\n");
            }
        }
            writer.close();
        }catch (Exception x){}
    }
    public void deleteCheckServer(){
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(songsLastUpdated);
        temp.removeAll(songsOnServer);
            for (String song: temp) {
                int index = songsOnDirectory.indexOf(song);
                try {
                    this.songs.get(index).deleteSong();
                }catch (Exception x){
                }
            }
        }
    public void refreshSyncFile(){
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(songsOnDirectory);
        for (String song:songsLastUpdated) {
            if(!temp.contains(song)) {
                temp.add(song);
            }
        }
        temp.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Comparable comp1 = o1.toLowerCase();
                Comparable comp2 = o2.toLowerCase();
                    return comp1.compareTo(comp2);
            }
        });
        String strToWrite ="";
        for (String item:temp) {
            strToWrite+=item+"\n";
        }
        try{
            FileWriter writer = new FileWriter("Sync.conf",false);
            writer.write(strToWrite);
            writer.close();
        }catch (Exception x){}
    }
    public int uploadFile(String serverURL,String filePath,int mod, int id) throws IOException {
        String strMod = "";
        switch (mod){
            case 0:
                strMod="Songs";
                break;
            case 1:
                strMod="PDFs";
                break;
            case 2:
                strMod="Movies";
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
    public String URLFormater(String url){
        return url.replace('\\','/');
    }
}
