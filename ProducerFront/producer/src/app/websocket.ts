import { Stomp } from '@stomp/stompjs';
// import * as SockJS from 'sockjs-client';
import * as SockJS from 'sockjs-client';
import Konva from 'Konva';
import { AppComponent } from './app.component';

export class WebSocketAPI {
    webSocketEndPoint: string = 'http://localhost:8070/ws';
    topic: string = "/topic/greetings";
    stompClient: any;
    appComponent: AppComponent;

    constructor(appComponent: AppComponent){
        this.appComponent = appComponent;
    }
    

    _connect() {
        console.log("Initialize WebSocket Connection");
        // let ws = new SockJS(this.webSocketEndPoint);
        let ws = new WebSocket("ws://localhost:8070/ws");
        this.stompClient = Stomp.over(ws);
        const _this = this;
        _this.stompClient.connect({}, function (frame:any) {
            _this.stompClient.subscribe(_this.topic, function (sdkEvent:any) {
                console.log(sdkEvent)
                _this.onMessageReceived(sdkEvent);
            });
            //_this.stompClient.reconnect_delay = 2000;
        }, this.errorCallBack);
    };

    _disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    // on error, schedule a reconnection attempt
    errorCallBack(error:any) {
        console.log("errorCallBack -> " + error)
        setTimeout(() => {
            this._connect();
        }, 5000);
    }
    /**
  * Send message to sever via web socket
  * @param {*} message 
  */
     _send(message:any) {
        console.log("calling logout api via web socket");
        this.stompClient.send("/app/hello", {}, JSON.stringify(message));
        // let data = JSON.stringify({
        //     'name' : this.name
        //   })
        // this.ws.send("/app/hello", {}, data);
    }

    onMessageReceived(message:any) {
        console.log("Message Recieved from Server :: " + message);
        this.appComponent.handleMessage(message.body);
    }
}