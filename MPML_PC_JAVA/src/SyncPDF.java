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

public class SyncPDF extends Thread {
    private static String UPLOAD_URL = "https://medialibraryweb.000webhostapp.com/upload.php";
    private ArrayList<PDF> pdfs;
    private ArrayList<String> pdfsLastUpdated;
    private ArrayList<String> pdfsOnDirectory;
    private ArrayList<String> pdfsOnServer;
    private ServerConnection dbConnection;
    private boolean localFileChanged = false;
    private boolean syncSet = false;

    public SyncPDF(ArrayList<PDF> pdfs, ArrayList<String> pdfsLastUpdated, boolean localFileChanged, boolean syncSet) {
        this.pdfs = pdfs;
        this.localFileChanged = localFileChanged;
        this.syncSet = syncSet;
        this.pdfsOnDirectory = new ArrayList<>();
        for (PDF pdf : pdfs) {
            pdfsOnDirectory.add(pdf.getName());
        }
        this.pdfsLastUpdated = pdfsLastUpdated;
        this.dbConnection = new ServerConnection();

        try {
            this.pdfsOnServer = dbConnection.getItemNameInServer(0);
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
        temp.addAll(pdfsLastUpdated);
        temp.removeAll(pdfsOnServer);
        if (temp.size() != 0) {
            deleteCheckServer(temp);
        }
        this.uploadPendingFiles();
    }

    public void newfileCheckLocal() {
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(pdfsOnDirectory);
        temp.removeAll(pdfsLastUpdated);
        try {
            //FileWriter writer = new FileWriter("PDFsToBeUploaded.conf");
            for (String pdf : temp) {
                int index = pdfsOnDirectory.indexOf(pdf);
                PDF pdfToUpload = this.pdfs.get(index);
                try {
                    int id = dbConnection.uploadPDF(pdfToUpload);
                    System.out.println(id);
                    this.uploadFile(UPLOAD_URL, URLFormater(pdfToUpload.getPath()), 0, id);
                } catch (Exception x) {
                    x.printStackTrace();
                    // writer.write(pdf+"\n");
                }
            }
            //writer.close();
        } catch (Exception x) {
        }
    }

    public void deleteCheckLocal() {
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(pdfsLastUpdated);
        temp.removeAll(pdfsOnDirectory);
        try {
            FileWriter writer = new FileWriter("PDFsToBeDeleted.conf");
            for (String pdf : temp) {
                try {
                    this.dbConnection.deleteItem(0, pdf);
                } catch (Exception x) {
                    writer.write(pdf + "\n");
                }
            }
            writer.close();
        } catch (Exception x) {
        }
    }

    public void deleteCheckServer(ArrayList<String> temp) {
        for (String pdf : temp) {
            int index = pdfsOnDirectory.indexOf(pdf);
            try {
                this.pdfs.get(index).deletePDF();
            } catch (Exception x) {
            }
        }
    }

    public int uploadFile(String serverURL, String filePath, int mod, int id) throws IOException {
        String strMod = "";
        switch (mod) {
            case 0:
                strMod = "PDFs";
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
        HashMap<Integer,String> missedSongs = this.dbConnection.getMissedFiles("pdfs");
        for (Map.Entry<Integer, String> item : missedSongs.entrySet()) {
            Integer id = item.getKey();
            String value = item.getValue();
            try{
                this.uploadFile(UPLOAD_URL, URLFormater(this.pdfs.get(this.pdfsOnDirectory.indexOf(value)).getPath()), 2, id);
            }catch (Exception e){}
        }
    }
    public String URLFormater(String url) {
        return url.replace('\\', '/');
    }
}
