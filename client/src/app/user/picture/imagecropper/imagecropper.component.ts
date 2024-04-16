import { Component, Input, Output, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ImageCroppedEvent } from 'ngx-image-cropper';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-imagecropper',
  templateUrl: './imagecropper.component.html',
  styleUrl: './imagecropper.component.css'
})
export class ImagecropperComponent {

  private sanitizer = inject(DomSanitizer)

  imgChangeEvt!: any ;

  
  cropImgPreview!: any;
  blob!:any

  @Input()
  pendingServer : boolean = false

  @Output()
  croppedImageUpload = new Subject<any>
  // objectUrl:string = ''
  onFileChange(event: any): void {
      this.imgChangeEvt = event;
  }
  cropImg(e: ImageCroppedEvent) {
    // console.log("event",e)
      // this.cropImgPreview = e.blob;
      
      // console.log("e.objectUrl",e.objectUrl)
      this.blob = e.blob
      let array = e.objectUrl?.split(":");
      this.cropImgPreview = this.sanitizer.bypassSecurityTrustUrl(e.objectUrl ?? "");
      // console.log("cropimg preview",this.cropImgPreview)
          // create a new file from the blob
      // const file = new File([e.blob], "captured.jpg", {type: 'image/jpg'})
      // console.log("file: ")
  }
  uploadImage(){
    this.croppedImageUpload.next(this.blob)
  }
  imgLoad() {
      // display cropper tool
  }
  initCropper() {
      // init cropper
  }
  
  imgFailed() {
      // error msg
  }

}
