import { Component } from '@angular/core';
import Konva from 'Konva';
import { Stack } from 'stack-typescript';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { WebSocketAPI } from './websocket';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'prodfront';

  productname:string=""
  sendStr:string = ""
  randomNumber:any;
  temp:any;
  idQueue:number=0
  idMachine:number=0
  ID:any
  positionRect = {x:0,y:0}
  positionCirc = {x:0,y:0}
  stage!: Konva.Stage;
  layer!: Konva.Layer;
  stack = new Stack<string>()
  input: any[] = [{
    "name":"",
    "color":""
  }];
  output: any[] = [{
    "name":"",
    "color":""
  }];
  webSocketAPI: any 
  greeting: any;
  name: string = "";
  answer:any
  run = false
  constructor(private http :HttpClient){}

  sendRequest(x: string) {
    if (x != '') {
      const headers = new HttpHeaders({ 'Content-Type': "application/text" })
      this.http.post("http://localhost:8070/get/expression", x,
        { headers: headers, responseType: 'text' })
        .subscribe(response => {
           this.answer = response
        }
          , (error) => {
            console.log(error);
          });
    }
  } 
  
  ngOnInit(): void {
    this.webSocketAPI = new WebSocketAPI(this);
    this.stage = new Konva.Stage({ container: 'container', width: 1500, height: 600 });
    this.layer = new Konva.Layer;
    this.stage.add(this.layer)
    this.stage.on('mousedown touchstart', (e) => {
      this.ID = e.target.attrs.id;
      if(this.ID!= undefined)
        {
            this.stack.push("#"+this.ID);
        }
        });
  }
  
  connect(){
    this.run = true
    this.webSocketAPI._connect();
    let that = this
    setTimeout(function(){
      that.sendRequest("run"),
      2000
    })
  }

  disconnect(){
    this.webSocketAPI._disconnect();
  }

  sendMessage(){
    this.webSocketAPI._send("run2");
  }

  str:string=""
  counter = 0
  handleMessage(message:any){
    this.greeting = message.split(",")
    let fun = this.greeting[0]
    switch (fun){
      case "color":
        this.str =  this.greeting[1]
        let color = this.greeting[2]
        var mach = this.layer.findOne(this.str)
        mach.setAttr("fill",color)
        break;
      case "input":
        let finish = "Q" + (this.idQueue-1)
        console.log(finish)
        let s = "Q" + this.greeting[1]
        if(s == "Q0"){
          let name = this.greeting[2]
          for(let i=0;i<this.input.length;i++)
          {
            if(this.input[i].name == name)
              this.input.splice(i,1)
          }
        }else if(s == finish)
        {
            let product ={
              "name":this.greeting[2],
              "color":this.greeting[3]
            }
            if (this.counter == 0)
              this.output.pop()
            this.output.push(product)
            this.counter++
        }
        break;
    }
    console.log(this.greeting)
  }

  circle() {
    this.temp = new Konva.Group({ 
      width: 30,
      height: 30,
      draggable: true,
    }); 
    this.temp.add(new Konva.Circle({
        id:"M"+this.idMachine,
        x:100,
        y:100,
        radius:30,
        fill: '#84f5ef',
        stroke: "black",
        strokeWidth: 2
    }));
    this.temp.add(new Konva.Text({
        text: "M"+this.idMachine,
        x:100,
        y:100,
        fontSize: 18,
        fontFamily: 'Calibre',
        fill: '#000',
        width: 6,
        padding: -10,
        align: 'center'
    }));
    this.sendStr = "addMachine"
    this.sendRequest(this.sendStr)
    this.idMachine++
    
    this.layer.add(this.temp);
  } 

  rectangle() {
    this.temp = new Konva.Group({
        width: 70,
        height: 70,
        x: 60,
        y: 100,
        draggable: true,
    }); 
    this.temp.add(new Konva.RegularPolygon({
      id:"Q"+this.idQueue,
      x: 60,
      y: 100,
      radius: 35,
      sides: 4,
      rotation: 45,
      name:"line",
      fill:"#ccc",
      stroke: "black",
      strokeWidth: 2
    }));
    this.temp.add(new Konva.Text({
        id: "Q"+this.idQueue,
        text: "Q"+this.idQueue,
        fontSize: 18,
        fontFamily: 'Calibre',
        fill: '#000',
        x: 50,
        y: 90,
        width: 25,
        padding: 0,
        align: 'center'
    }));
    this.sendStr = "addQueue"
    this.sendRequest(this.sendStr)
    this.idQueue++
    this.layer.add(this.temp);
  }
  
  line(){
    var a = this.stack.pop();
    var b = this.stack.pop();
    if((a.includes('M',0)&&b.includes('Q',0))||(a.includes('Q',0)&&b.includes('M',0)))
    {
          var shape = this.stage.find(a)[0];
          var rectangle = this.stage.find(b)[0];
          this.positionRect = rectangle.getAbsolutePosition()
          this.positionCirc = shape.getAbsolutePosition()
          var arrow = new Konva.Arrow({
            points: [this.positionRect.x,this.positionRect.y,this.positionCirc.x,this.positionCirc.y],
            pointerLength: 10,
            pointerWidth: 10,
            fill: 'black',
            stroke: 'black',
            strokeWidth: 4
          });
          this.layer.add(arrow)
          this.sendStr = "link," + b + "," + a
          this.sendRequest(this.sendStr)
    }
    
  }

  reset(){
    this.sendRequest("reset")
    window.location.reload()
    this.disconnect()
  }

  re: any[] = [{
    "name":"",
    "color":""
  }];
  replay(){
    let len = this.output.length
    for(let i = 0; i<len; i++){
      this.re.push(this.output.pop())
    }
    for(let i = 0;i<len;i++){
        this.input.push(this.re.pop())
    }
    let that = this
    setTimeout(function(){
      that.sendRequest("replay"),
      2000
    })

  }
  generateRandomColor(){
    let maxVal = 0xFFFFFF;
    this.randomNumber = Math.random() * maxVal; 
    this.randomNumber = Math.floor(this.randomNumber);
    this.randomNumber = this.randomNumber.toString(16);
    let randColor = this.randomNumber.padStart(6, 0);  
    console.log(`#${randColor.toUpperCase()}`) 
    return `#${randColor.toUpperCase()}`
  }
  count = 0
  addproduct(){
    let product ={
      "name":this.productname,
      "color":this.generateRandomColor()
    }
    this.sendStr = "addProduct," + product.name + "," + product.color
    this.sendRequest(this.sendStr)
    if(this.count == 0)
      this.input.pop()
    this.input.push(product)
    this.count++
    this.productname = ""
  }
}