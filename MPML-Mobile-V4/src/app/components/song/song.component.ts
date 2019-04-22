import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform,IonList } from '@ionic/angular';
//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from 'src/app/service/fs.service';
export class Song{
  constructor(public name?, public album?, public artist?, public duration?, public size?,public id?, public available?:boolean){
  }
}
  

@Component({
  selector: 'app-song',
  templateUrl: './song.component.html',
  styleUrls: ['./song.component.scss'],
  providers:[FsService,]
})

export class SongComponent{
    username:string;
    password:string;
    @ViewChild('dynamicList') dynamiclist:IonList;
    songs :Song[];
    sharedId:string;
    selectedSongToShare:Song;
    songPlaying:string;
    constructor(private platform: Platform, private fs:FsService){
     platform.ready()
      .then(() => {
        this.fs.getSongs().then(songs=>this.songs=songs);
      });
    }
    public async deleteSong(index:number){
      this.songs.splice(index, 1);
      this.fs.deleteFile(0,this.songs[index].id);
      await this.dynamiclist.closeSlidingItems();
    }

    public openItem($item){
      let available = this.fs.openFile(0, $item.name);
      if(!available){
        window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/Songs/"+$item.id+".mp3",'_system','location=yes');
      }
      /*if($item.available){
       this.fs.openFile(0, $item.name);
      }
      else{
        window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/Songs/"+$item.id+".mp3",'_system','location=yes');
      }*/
    }

    public downloadSong($song){
      this.platform.ready().then(()=>{
        this.fs.downloadFile(0,$song.id,$song.name);
      })
    }

    sortItems(tag){
      switch(tag){
        case 'name':
          this.songs = this.songs.sort(function(a, b){
            if(a.name < b.name)return -1;else return 1;
          });
        break;
        case 'album':
          this.songs = this.songs.sort(function(a, b){
            if(a.album < b.album)return -1;else return 1;
          });
        break;
        case 'artist':
          this.songs = this.songs.sort(function(a, b){
            if(a.artist < b.artist)return -1;else return 1;
          });
        break;
        case 'duration':
          this.songs = this.songs.sort(function(a, b){
              return a.duration - b.duration;
          });
        break;
        case 'size':
          this.songs = this.songs.sort(function(a, b){
              return a.size - b.size;
          });
        break;
      }
    }
    public reload(){
      this.fs.loadSongsFromServer().then(songs=>this.songs=songs);
    }
  public ionViewDidEnter(): void {
    if(this.songs==null){
      this.fs.loadSongsFromServer().then(songs=>this.songs=songs);
    }
  }
  public share(){
    this.fs.share(0,this.selectedSongToShare.id,this.sharedId);
  }
}
