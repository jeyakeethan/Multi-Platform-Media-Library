import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform } from '@ionic/angular';
import { FsService } from 'src/app/service/fs.service';

export class Video{
  constructor(public name?, public date?, public duration?, public size?, public id?, public cover?, public available?){
  }
}

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss'],
  providers:[FsService,]
})

export class VideoComponent{
  username:string;
  password:string;
  videos :Video[];
  videosRetrieved :Video[];
  sharedId:string;
  selectedVideoToShare:Video;
  videoPlaying:string;
  public searching: Boolean = false;
  public searchTerm:string = "";
  constructor(private platform: Platform, private fs:FsService){
  }
  onInit(){
    this.fs.loadMoviesFromServer().then(videos=>this.videosRetrieved=videos);
    this.videos = this.videosRetrieved;
    /*this.platform.ready().then(()=>{
      this.videosRetrieved.forEach(element => {
        element.available=this.fs.checkFile(1,element.name);
      });
    });*/
  }

  public cancelSearch(){
    this.searching = false;
    this.videos = this.videosRetrieved;
  }
  public setFilteredItems(){
    this.searching = true;
    if(this.searchTerm==""){
      this.videos = this.videosRetrieved;
    }
    else{
      var key = this.searchTerm.toLowerCase();
      this.videos = this.videosRetrieved.filter(video => {
        return String(video.name).toLowerCase().startsWith(key);
      });
    }
    this.searching = false;
  }

  public async deleteVideo(index:number){
    //var result = confirm("Want to delete?");
    //if (result) {
      this.videos.splice(index, 1);
      this.fs.deleteFile(1,index);
    //}
  }

  public openItem($item){
    let available = this.fs.openFile(1, $item.name);
    if(!available){
      window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/Movies/"+$item.id+".mp4",'_system','location=yes');
    }
  }

  public downloadVideo($video){
    this.platform.ready().then(()=>{
      this.fs.downloadFile(1,$video.id,$video.name);
    })
  }
  sortItems(tag){
    switch(tag){
      case 'name':
        this.videos = this.videos.sort(function(a, b){
          if(a.name < b.name)return -1;else return 1;
        });
      break;
      case 'date':
        this.videos = this.videos.sort(function(a, b){
          if(a.date < b.date)return -1;else return 1;
        });
      break;
      case 'duration':
        this.videos = this.videos.sort(function(a, b){
            return a.duration - b.duration;
        });
      break;
      case 'size':
        this.videos = this.videos.sort(function(a, b){
            return a.size - b.size;
        });
      break;
    }
  }
  public async reload(){
    if(FsService.user!=null){
      this.fs.loadMoviesFromServer().then(videos=>this.videosRetrieved=videos);
      this.videos = this.videosRetrieved;
    }else{
      alert("Go to Setting tab and login First!");
    }
  }
public ionViewDidLoad(): void {
  if(this.videos==null){
    this.fs.loadMoviesFromServer().then(videos=>this.videosRetrieved=videos);
    this.videos = this.videosRetrieved;
  }
}
public share(){
  this.fs.share(1,this.selectedVideoToShare.id,this.sharedId);
}
public parseDuration(sec_num:number):String{
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);
    let h = String(hours);
    let m = String(minutes);
    let s = String(seconds);
    if (hours   < 10) {h   = "0"+h;}
    if (minutes < 10) {m = "0"+m;}
    if (seconds < 10) {s = "0"+s;}
    let h1 = h == "00"?"":h+":";
    return h1 +m+':'+s;
}
}
